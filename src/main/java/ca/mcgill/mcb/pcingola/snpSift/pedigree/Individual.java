package ca.mcgill.mcb.pcingola.snpSift.pedigree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import ca.mcgill.mcb.pcingola.ped.Sex;
import ca.mcgill.mcb.pcingola.ped.TfamEntry;

/**
 * An individual in the pedigree
 * 
 * Individuals are like TfamEntries but have drawing info (coordinates, color, etc.)
 * 
 * @author pablocingolani
 */
public class Individual implements Comparable<Individual> {

	HashSet<Individual> childs;
	ArrayList<Individual> childsSorted;
	int order = Integer.MIN_VALUE;
	int depth = -1;
	int descendants = Integer.MIN_VALUE;
	String id, familyId;
	String label;
	Point position;
	Sex sex;
	String color = "white";
	Boolean affected = null;
	Individual father, mother;

	public Individual(TfamEntry tfamEntry) {
		id = tfamEntry.getId();
		sex = tfamEntry.getSex();
		familyId = tfamEntry.getFamilyId();
		label = id;

		if (tfamEntry.isMissing()) affected = null;
		else affected = tfamEntry.isCase();

		childs = new HashSet<Individual>();
		position = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
		this.label = id;
	}

	protected void addChild(Individual ind) {
		childs.add(ind);
		childsSorted = null;
	}

	/**
	 * Depth in the graph. Depth is zero for root nodes
	 * @return
	 */
	public int calcDepth() {
		int maxDepth = Math.max(0, depth);

		if (mother != null) maxDepth = Math.max(maxDepth, mother.calcDepth() + 1);
		if (father != null) maxDepth = Math.max(maxDepth, father.calcDepth() + 1);

		depth = maxDepth;
		return depth;
	}

	@Override
	public int compareTo(Individual ind) {
		return id.compareTo(ind.getId());
	}

	/**
	 * Number of descendants
	 * @param ind
	 * @return
	 */
	int descendants() {
		if (descendants >= 0) return descendants;

		descendants = getChilds().size();
		for (Individual ch : getChilds())
			descendants += ch.descendants();

		return descendants;
	}

	public Boolean getAffected() {
		return affected;
	}

	public Collection<Individual> getChilds() {
		if (childsSorted == null) {
			childsSorted = new ArrayList<Individual>();
			childsSorted.addAll(childs);
			Collections.sort(childsSorted);
		}
		return childsSorted;
	}

	/**
	 * Get only childs from this spouse
	 * @param spouse
	 * @return
	 */
	public ArrayList<Individual> getChilds(Individual spouse) {
		ArrayList<Individual> sharedChilds = new ArrayList<Individual>();

		if (spouse != null) {
			sharedChilds.addAll(getChilds());
			sharedChilds.retainAll(spouse.getChilds());
		} else {
			// Look for child where the other parent os not in the pedigree (i.e. is null)
			for (Individual ch : getChilds())
				if ((ch.getMother() == null) || (ch.getFather() == null)) sharedChilds.add(ch);
		}
		return sharedChilds;
	}

	public String getColor() {
		return color;
	}

	public int getDepth() {
		return depth;
	}

	public String getFamilyId() {
		return familyId;
	}

	public Individual getFather() {
		return father;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Individual getMother() {
		return mother;
	}

	//	public int getPedigreeNumber() {
	//		return pedigreeNumber;
	//	}

	public int getOrder() {
		return order;
	}

	public Point getPosition() {
		return position;
	}

	public Sex getSex() {
		return sex;
	}

	protected boolean hasOrder() {
		return order != Integer.MIN_VALUE;
	}

	public boolean hasPosition() {
		return !((position.x == Integer.MIN_VALUE) || (position.y == Integer.MIN_VALUE));
	}

	/**
	 * Is 'this' descendant from 'ind' (e.g. son, grand son, etc.)
	 * @param ind
	 * @return
	 */
	public boolean isDescendant(Individual ind) {
		return (mother == ind) //
				|| (father == ind) //
				|| (mother != null ? mother.isDescendant(ind) : false)//
				|| (father != null ? father.isDescendant(ind) : false);
	}

	public boolean isFemale() {
		return sex == Sex.Female;
	}

	public boolean isMale() {
		return sex == Sex.Male;
	}

	/**
	 * Is this a root node?
	 * @return
	 */
	public boolean isRoot() {
		return (mother == null) && (father == null);
	}

	/**
	 * One of the childs has this other individual as parent
	 * @param ind
	 * @return
	 */
	boolean isSpouse(Individual ind) {
		for (Individual ch : childs) {
			if ((ch.getMother() != null) && (ch.getMother().getId().equals(ind.getId()))) return true;
			if ((ch.getFather() != null) && (ch.getFather().getId().equals(ind.getId()))) return true;
		}
		return false;
	}

	protected void removeChild(Individual ind) {
		childs.remove(ind);
		childsSorted = null;
	}

	Individual rootMother() {
		if (mother == null) return null;
		Individual mom = mother;
		Individual root = mom.rootMother();
		if (root != null) return root;
		return mom;
	}

	public void setAffected(Boolean affected) {
		this.affected = affected;
	}

	//	public void setChilds(ArrayList<Individual> childs) {
	//		this.childs = childs;
	//	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setDepth(int depth) {
		this.depth = depth;
		for (Individual ch : childs)
			ch.setDepth(depth + 1);
	}

	public void setFather(Individual father) {
		if ((father != null) && (!father.isMale())) throw new RuntimeException("Error: Father is not male");

		// Remove as child from previous father (if any)
		if (this.father != null) this.father.removeChild(this);

		this.father = father;

		// Add as child of new father
		if (father != null) father.addChild(this);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setMother(Individual mother) {
		if ((mother != null) && mother.isMale()) throw new RuntimeException("Error: Individual '" + id + "' has non-female mother '" + mother.getId() + "'");

		// Remove as child from previous mother (if any)
		if (this.mother != null) this.mother.removeChild(this);

		this.mother = mother;

		// Add as child of new mother
		if (mother != null) mother.addChild(this);
	}

	public void setOrder(int order) {
		this.order = order;
	}

	//	public void setPedigreeNumber(int pedigreeNumber) {
	//		this.pedigreeNumber = pedigreeNumber;
	//	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	/**
	 * Size by depth
	 * @param sizeByLevel
	 */
	public void sizeByDepth(int sizeByLevel[]) {
		sizeByLevel[depth]++;
		for (Individual ch : childs) {
			ch.sizeByDepth(sizeByLevel);
		}
	}

	@Override
	public String toString() {
		return id + (hasPosition() ? " [" + position.x + " , " + position.y + "]" : "");
	}

	public String toStringTree() {
		StringBuffer sb = new StringBuffer();

		sb.append(id + " ");
		if (!childs.isEmpty()) {
			sb.append("[ ");
			for (Individual ch : getChilds())
				sb.append(ch.toStringTree() + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
		}

		return sb.toString();
	}

}
