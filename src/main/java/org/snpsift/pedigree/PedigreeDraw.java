package org.snpsift.pedigree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.snpeff.ped.PedPedigree;
import org.snpeff.ped.Sex;
import org.snpeff.ped.TfamEntry;
import org.snpeff.util.Gpr;

/**
 * Draws a pedigree using SVG
 * 
 * @author pablocingolani
 */
public class PedigreeDraw {

	public static boolean debug = false;

	int Y_OFFSET = 25;
	int X_OFFSET = 80;

	int INDIVIDUAL_SIZE = 20;
	int INDIVIDUAL_HALF_SIZE = INDIVIDUAL_SIZE / 2;
	int FEMALE_RADIUS = INDIVIDUAL_SIZE / 2;
	int MALE_SIZE = INDIVIDUAL_SIZE;
	int STROKE_WIDTH = 2;

	int FONT_SIZE = 10;
	int LABEL_DELTA_X = 20;
	int LABEL_DELTA_Y = 20;

	int STEP_X = 25;
	int STEP_Y = 200;

	int sizeX, sizeY;

	String GEN_LINE_COLOR = "grey";
	String LINE_COLOR = "black";
	String STROKE_COLOR = "black";

	String BG_COLOR = "white";
	String BG_COLOR_SEQUENCED = "pink";
	String FILL_COLOR_FEMALE = "pink";
	String FILL_COLOR_MALE = "blue";

	String COLOR_SEQUENCED = "blue";

	String FILL_COLOR_TRUE = "red";
	String FILL_COLOR_FALSE = "green";

	String MARK_COLOR = "green";
	String MARK_BG_COLOR = "white";

	int MARK_WIDTH = 2;

	PedPedigree pedigree;
	int maxDepth;
	int labelCount[]; // Label count by depth
	int couplesByDepth[]; // Number of couples in each depth
	HashSet<String> elements = new HashSet<String>(); // All elements drawn
	ArrayList<String> elementsSorted = new ArrayList<String>();
	HashMap<String, Integer> coupleNum = new HashMap<String, Integer>();
	ArrayList<Individual> individuals;
	HashMap<String, Individual> individualsById = new HashMap<String, Individual>();
	HashSet<String> assignedPos = new HashSet<String>();

	public PedigreeDraw(PedPedigree pedigree) {
		this.pedigree = pedigree;
		init();
	}

	public PedigreeDraw(String tfamFileName) {
		pedigree = new PedPedigree();
		pedigree.loadTfam(tfamFileName);
		init();
	}

	/**
	 * Add a line
	 * @param line
	 */
	void add(String line) {
		elements.add(line);
		elementsSorted.add(line);
	}

	int assign(Individual ind, int pos) {
		if (ind.hasPosition()) assignedPos.remove(ind.getPosition().x + "\t" + ind.getDepth()); // Remove old key

		int max = Math.max(ind.getPosition().x, pos);
		while (assignedPos.contains(max + "\t" + ind.getDepth()))
			max++;

		ind.getPosition().x = max;
		assignedPos.add(max + "\t" + ind.getDepth());

		if (debug) Gpr.debug(Gpr.tabs(ind.getDepth()) + ind.getId() + "\t" + ind.getDepth() + " , " + ind.getPosition().x);

		return max;
	}

	/**
	 * Assign order
	 * @return
	 */
	public int assignOrder() {
		int order = 0;
		for (Individual ind : individuals)
			ind.setOrder(order++);

		return order;
	}

	/**
	 * Assign X position. 
	 */
	int assignXpos() {
		// Sort individuals by number pf descendants
		ArrayList<Individual> indByDesc = new ArrayList<Individual>();
		indByDesc.addAll(individuals);
		Collections.sort(indByDesc, new Comparator<Individual>() {

			@Override
			public int compare(Individual o1, Individual o2) {
				int comp = o2.descendants() - o1.descendants();
				if (comp != 0) return comp;
				return o1.getId().compareTo(o2.getId());
			}
		});

		// Assign positions
		int maxX = 0;
		for (int depth = 0; depth <= maxDepth; depth++) {
			maxX = maxPositionX(depth);
			for (Individual ind : indByDesc) {
				if (!ind.hasPosition() && (ind.getDepth() == depth)) maxX = assignXpos(ind, maxX);
			}
		}

		return maxX;
	}

	int assignXpos(Individual ind, Individual spouse, int minPos) {
		// Childs with this father
		Collection<Individual> sharedChilds = (spouse != null ? ind.getChilds(spouse) : ind.getChilds());
		int maxX = minPos + 1;

		if (sharedChilds.isEmpty()) {
			maxX = assign(ind, minPos + 1);
			return maxX;
		}

		int childsMin = maxX;
		for (Individual ch : sharedChilds)
			maxX = assignXpos(ch, maxX);
		int childsMax = maxX;
		int mid = (childsMax + childsMin) / 2;

		int paren1 = Math.max(mid - 1, minPos);
		paren1 = assign(ind, paren1);
		int paren2 = Math.max(paren1 + 2, minPos);
		if (spouse != null) assign(spouse, paren2);

		maxX = Math.max(maxX, paren2);

		return maxX;
	}

	/**
	 * Assign positions to a nuclear family (mom+dad+childs)
	 * @param parent1
	 * @param parent2
	 */
	int assignXpos(Individual ind, int minPos) {
		if (ind.hasPosition()) return Math.max(ind.getPosition().x, minPos);

		int maxX = minPos;
		// Find all fathers
		List<Individual> spouses = findSpouses(ind);
		if (spouses.isEmpty()) {
			maxX = assignXpos(ind, null, maxX);
		} else {
			for (Individual spouse : spouses)
				maxX = assignXpos(ind, spouse, maxX);
		}

		return maxX;
	}

	/**
	 * Assign Y position. 
	 * This is fairly easy, since it only depends on the 'generation' (or graph depth)
	 */
	void assignYpos() {
		for (Individual ind : individuals)
			ind.getPosition().y = ind.getDepth();
	}

	/**
	 * Max depth in this pedigree
	 * @return
	 */
	public int calcDepth() {
		int maxDepth = 0;
		for (Individual indzz : individuals) {
			for (Individual ind : individuals) {
				int depth = ind.calcDepth();

				Individual mother = ind.getMother();
				if (mother != null) {
					int dmo = depth - 1;
					if (dmo > mother.getDepth()) mother.setDepth(dmo);
				}

				Individual father = ind.getFather();
				if (father != null) {
					int dfa = depth - 1;
					if (dfa > father.getDepth()) father.setDepth(dfa);
				}

				maxDepth = Math.max(maxDepth, ind.getDepth());
			}
		}
		return maxDepth;

	}

	public void circle(int x, int y, int r, String strokeColor, int strokeWidth, String fillColor) {
		add("<circle cx=\"" + x + "\" cy=\"" + y + "\" r=\"" + r + "\" stroke=\"" + strokeColor + "\" stroke-width=\"" + strokeWidth + "\" fill=\"" + fillColor + "\"/>\n");
	}

	/**
	 * Color individuals by affected status
	 */
	public void colorAffected() {
		// Color individuals
		for (TfamEntry tfam : pedigree) {
			Individual ind = individualsById.get(tfam.getId());

			// Change color
			if (ind.getAffected() != null) {
				if (ind.getAffected()) ind.setColor("red");
				else ind.setColor("green");
			} else ind.setColor("grey");
		}
	}

	String coupleKey(Individual i1, Individual i2) {
		if (i2 == null) i2 = i1;
		if (i1.compareTo(i2) <= 0) return i1.getId() + "\t" + i2.getId();
		return i2.getId() + "\t" + i1.getId();
	}

	/**
	 * Calculate how many couple each level has
	 */
	void couplesByDepth() {
		couplesByDepth = new int[maxDepth + 1];

		for (Individual ind : individuals) {
			List<Individual> spouses = findSpouses(ind);
			spouses.add(null); // Children with a parent not in pedigree

			for (Individual spouse : spouses) {
				String coupleKey = coupleKey(ind, spouse);

				if (coupleNum.containsKey(coupleKey)) continue;
				if (ind.getChilds(spouse).isEmpty()) continue;

				int depth = ind.getDepth();
				int coupleN = ++couplesByDepth[depth];
				coupleNum.put(coupleKey, coupleN);
			}
		}
	}

	/**
	 * Draw a diamond (unknown sex)
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param strokeColor
	 * @param strokeWidth
	 * @param fillColor
	 */
	public void diamond(int x, int y, int width, int height, String strokeColor, int strokeWidth, String fillColor) {
		int w2 = width / 2, h2 = height / 2;

		add("<path " //
				+ "d=\"M " + x + " y=" + y //
				+ " L " //
				+ w2 + " " + h2 //
				+ w2 + " " + -h2 //
				+ -w2 + " " + -h2 //
				+ -w2 + " " + h2 //
				+ "\"" //
				+ "stroke=\"" + strokeColor + "\"" // 
				+ "stroke-width=\"" + strokeWidth + "\"" // 
				+ (fillColor != null ? " fill=\"" + fillColor + "\"" : "") // 
				+ "/>");
	}

	/**
	 * Draw the full pedigree as an SVG graph
	 * @return
	 */
	public String drawSvg() {
		// Calculate depth
		maxDepth = calcDepth();
		labelCount = new int[maxDepth + 1];
		couplesByDepth();

		assignOrder();

		//---
		// Draw individuals
		//---
		assignYpos();
		assignXpos();
		sizeY = scaleY(STEP_Y, Y_OFFSET);
		sizeX = scaleX(STEP_X, X_OFFSET);

		// Draw
		for (Individual ind : individuals)
			drawSvg(ind);

		//---
		// Draw lines
		//---
		for (Individual ind : individuals) {
			Individual mother = ind.getMother();
			Individual father = ind.getFather();

			if ((father != null) & (mother != null)) line(father, mother, ind);
			else if (mother != null) line(mother, mother, ind);
			else if (father != null) line(father, father, ind);
		}

		// Dotted lines and generations
		for (int i = 0; i <= maxDepth; i++) {
			int y = posY(i);
			lineDashed(new Point(0, y), new Point(sizeX, y), GEN_LINE_COLOR);
			label(new Point(LABEL_DELTA_X, y), "Gen_" + (i + 1));
		}

		// Create SVG document 
		StringBuffer out = new StringBuffer();
		out.insert(0, "<?xml version=\"1.0\" standalone=\"no\"?>\n" //
				+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" //
				+ "<svg width=\"100%\" height=\"100%\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" //
		);

		for (String line : elementsSorted) {
			if (elements.contains(line)) {
				out.append(line);
				elements.remove(line); // Make sure it is not added twice
			}
		}
		out.append("</svg>\n");

		return out.toString();
	}

	/**
	 * Draw an individual
	 * @param ind
	 * @return
	 */
	void drawSvg(Individual ind) {
		if (!ind.hasPosition()) {
			Gpr.debug("Skipping individual " + ind);
			return;
		}

		switch (ind.getSex()) {
		case Male:
			male(ind);
			break;

		case Female:
			female(ind);
			break;

		default:
			unknown(ind);
		}
	}

	/**
	 * Draw each family
	 */
	public void drawSvgByFamily(String outdir, String info) {
		StringBuffer index = new StringBuffer();

		//---
		// Create and load pedigree
		//---

		// Sort by pedigree number
		ArrayList<PedPedigree> families = new ArrayList<PedPedigree>();
		families.addAll(pedigree.families());
		Collections.sort(families);

		//---
		// Draw all pedigrees
		//---
		index = new StringBuffer();
		index.append("\n<center><b> Pedigree </b><center>\n");
		index.append("\n<pre>\n" + info + "\n</pre>\n");

		for (PedPedigree family : families) {
			String famId = family.getFamilyId();
			String familySvgFile = famId + ".svg";

			// Draw family
			PedigreeDraw pedigreeDraw = new PedigreeDraw(family);

			// Copy colors into new pedigree
			for (Individual ind : individuals) {
				Individual indf = pedigreeDraw.get(ind.getId());
				if (indf != null) indf.setColor(ind.getColor());
			}

			String svg = pedigreeDraw.drawSvg();

			Gpr.toFile(outdir + "/" + familySvgFile, svg);

			// Add data to HTML file
			index.append("<p><center> Pedigree: " + famId + "</center>\n");
			index.append("\n<iframe src=\"" + familySvgFile + "\" width=\"" + pedigreeDraw.getSizeX() + "\" height=\"" + pedigreeDraw.getSizeY() + "\"> </iframe> <br>\n");
		}

		Gpr.toFile(outdir + "/index.html", index);
	}

	public void female(Individual ind) {
		Point p = ind.getPosition();
		String color = (ind.getColor() != null ? ind.getColor() : BG_COLOR);
		circle(p.x, p.y, FEMALE_RADIUS, STROKE_COLOR, STROKE_WIDTH, color);
		label(ind);
	}

	public List<Individual> findSpouses(Individual ind) {
		LinkedList<Individual> spouses = new LinkedList<Individual>();

		for (Individual ind2 : individuals)
			if ((ind != ind2) && ind.isSpouse(ind2)) spouses.add(ind2);

		//Gpr.debug("Ind: " + ind.getId() + "\tNum spouses: " + spouses.size());
		return spouses;
	}

	public Individual get(String id) {
		return individualsById.get(id);
	}

	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	/**
	 * Initialize
	 */
	void init() {
		individualsById = new HashMap<String, Individual>();
		individuals = new ArrayList<Individual>();

		// Create 'individuals' 
		for (TfamEntry tf : pedigree) {
			Individual ind = new Individual(tf);
			individuals.add(ind);
			individualsById.put(ind.getId(), ind);
		}
		Collections.sort(individuals);

		// Set father & mother
		LinkedList<Individual> toAdd = new LinkedList<Individual>();
		for (Individual ind : individuals) {
			String indId = ind.getId();

			// Set father
			String fid = pedigree.get(indId).getFatherId();
			Individual father = individualsById.get(fid);
			if ((fid != null) && (father == null)) {
				// Father not found? Add
				father = new Individual(new TfamEntry(ind.getFamilyId(), fid, null, null, Sex.Male, 0));
				toAdd.add(father);
			}
			ind.setFather(father);

			// Set mother
			String mid = pedigree.get(indId).getMotherId();
			Individual mother = individualsById.get(mid);
			if ((mid != null) && (mother == null)) {
				// Mother not found? Add
				mother = new Individual(new TfamEntry(ind.getFamilyId(), mid, null, null, Sex.Female, 0));
				toAdd.add(mother);
			}
			ind.setMother(mother);
		}

		// Add all missing parents
		for (Individual ind : toAdd) {
			individuals.add(ind);
			individualsById.put(ind.getId(), ind);
		}
	}

	void label(Individual ind) {
		Point p = new Point(ind.getPosition());

		int off = FONT_SIZE * (ind.hashCode() % 2);

		if ((ind.hashCode() % 4) <= 1) p.y = p.y - INDIVIDUAL_SIZE - FONT_SIZE - FONT_SIZE / 3 - off;
		else p.y = p.y + off;

		p.x += INDIVIDUAL_SIZE / 2;

		label(p, ind.getLabel());
	}

	public void label(Point p, String label) {
		add("<text x=\"" + (p.x - LABEL_DELTA_X) + "\" y=\"" + (p.y + LABEL_DELTA_Y) + "\" font-family=\"Verdana\" font-size=\"" + FONT_SIZE + "\" fill=\"black\"> " + label + " </text>\n");
	}

	/**
	 * Line between Mother-Father and Child
	 * @param parent1
	 * @param parent2
	 * @param child
	 * @return
	 */
	public void line(Individual parent1, Individual parent2, Individual child) {
		//---
		// Join parents using a three lines
		//---
		int mid1x = (parent1.getPosition().x + parent2.getPosition().x) / 2;
		int offset = offset(parent1, parent2);
		int mid1y = (parent1.getPosition().y + parent2.getPosition().y) / 2 + offset;
		Point mid1 = new Point(mid1x, mid1y);

		Point underParent1 = new Point(parent1.getPosition().x, mid1y);
		Point underParent2 = new Point(parent2.getPosition().x, mid1y);

		line(plus(parent1.getPosition(), 0, INDIVIDUAL_HALF_SIZE), underParent1);
		line(plus(parent2.getPosition(), 0, INDIVIDUAL_HALF_SIZE), underParent2);
		line(underParent1, underParent2);

		// Mid Y point
		int mid2y = child.getPosition().y - offset;
		Point mid2 = new Point(mid1x, mid2y);
		line(mid1, mid2);

		// Line to child
		Point mid3 = new Point(child.getPosition().x, mid2y);
		line(mid2, mid3);
		line(mid3, plus(child.getPosition(), 0, -INDIVIDUAL_HALF_SIZE));
	}

	public void line(Point p1, Point p2) {
		add("<line x1=\"" + p1.x + "\" y1=\"" + p1.y + "\" x2=\"" + p2.x + "\" y2=\"" + p2.y + "\" stroke-width=\"1\" stroke=\"" + LINE_COLOR + "\" />\n");
	}

	public void line(Point p1, Point p2, String color) {
		add("<line x1=\"" + p1.x + "\" y1=\"" + p1.y + "\" x2=\"" + p2.x + "\" y2=\"" + p2.y + "\" stroke-width=\"1\" stroke=\"" + color + "\" />\n");
	}

	public void lineDashed(Point p1, Point p2, String color) {
		add("<line x1=\"" + p1.x + "\" y1=\"" + p1.y + "\" x2=\"" + p2.x + "\" y2=\"" + p2.y + "\" style=\"stroke-dasharray: 2, 10; stroke-width: 1; stroke: " + color + ";\" />\n");
	}

	public void male(Individual ind) {
		Point p = ind.getPosition();
		String color = (ind.getColor() != null ? ind.getColor() : BG_COLOR);
		square(p.x - MALE_SIZE / 2, p.y - MALE_SIZE / 2, MALE_SIZE, MALE_SIZE, STROKE_COLOR, STROKE_WIDTH, color);
		label(ind);
	}

	/**
	 * Maximum position for 'depth'
	 * @param depth
	 * @return
	 */
	int maxPositionX(int depth) {
		int maxX = 0;
		for (Individual ind : individuals)
			if ((ind.getDepth() == depth) && ind.hasPosition()) maxX = Math.max(maxX, ind.getPosition().x);
		return maxX;
	}

	/**
	 * Move if the object is located to the left of posX
	 * @param xpos
	 * @param deltaX
	 */
	protected void moveIfLeft(int posX, int deltaX, Set<Individual> except) {
		for (Individual ind : individuals) {
			if (ind.hasPosition() && (ind.getPosition().x <= posX) && (!except.contains(ind))) {
				ind.getPosition().x += deltaX;
				Gpr.debug("Moved Left: " + ind);
			}
		}
	}

	/**
	 * Move if the object is located to the right of posX
	 * @param xpos
	 * @param deltaX
	 */
	protected void moveIfRight(int posX, int deltaX, Set<Individual> except) {
		for (Individual ind : individuals) {
			if (ind.hasPosition() && (ind.getPosition().x >= posX) && (!except.contains(ind))) {
				ind.getPosition().x += deltaX;
				Gpr.debug("Moved right: " + ind);
			}
		}
	}

	/**
	 * Calculate an offset
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	int offset(Individual parent1, Individual parent2) {
		int off = 0;

		int min = Math.max(FEMALE_RADIUS, MALE_SIZE / 2);
		int max = STEP_Y / 2 - min / 2;

		int maxCouples = couplesByDepth[parent1.getDepth()];
		if (maxCouples == 1) off = (STEP_Y / 4);
		else {
			String key = coupleKey(parent1, parent2);
			int coupleN = coupleNum.get(key);
			off = coupleN * (max - min) / (maxCouples + 1);
		}

		off += min;
		return off;
	}

	Point plus(Point p, int x, int y) {
		return new Point(p.x + x, p.y + y);
	}

	/**
	 * Position based on depth
	 * @param stepY
	 * @param depth
	 * @return
	 */
	int posY(int depth) {
		return Y_OFFSET + depth * STEP_Y;
	}

	/**
	 * Scale X axis
	 * @param stepX
	 * @return
	 */
	int scaleX(int stepX, int offset) {
		int min = Integer.MAX_VALUE;
		for (Individual i : individuals)
			if (i.hasPosition()) min = Math.min(min, i.getPosition().x);

		int max = 0;
		for (Individual i : individuals) {
			if (i.hasPosition()) {
				i.getPosition().x = stepX * (i.getPosition().x - min) + offset;
				max = Math.max(max, i.getPosition().x);
			}
		}
		return max + 2 * X_OFFSET;
	}

	/**
	 * Scale Y axis
	 * @param stepY
	 * @return
	 */
	int scaleY(int stepY, int offset) {
		int max = 0;
		for (Individual i : individuals) {
			i.getPosition().y = stepY * i.getPosition().y + offset;
			max = Math.max(max, i.getPosition().y);
		}

		return max + 2 * Y_OFFSET;
	}

	/**
	 * Show a mark around an individual
	 * @param ind
	 */
	void showMark(Individual ind, String color) {
		Point p = ind.getPosition();
		square(p.x - MALE_SIZE, p.y - MALE_SIZE, MALE_SIZE * 2, MALE_SIZE * 2, color, MARK_WIDTH, MARK_BG_COLOR);
	}

	public void square(int x, int y, int width, int height, String strokeColor, int strokeWidth, String fillColor) {
		if (fillColor != null) add("<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" stroke=\"" + strokeColor + "\" stroke-width=\"" + strokeWidth + "\" fill=\"" + fillColor + "\"/>");
		else add("<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" stroke=\"" + strokeColor + "\" stroke-width=\"" + strokeWidth + "\"/>");
	}

	public void unknown(Individual ind) {
		Point p = ind.getPosition();
		String color = (ind.getColor() != null ? ind.getColor() : BG_COLOR);
		diamond(p.x - MALE_SIZE / 2, p.y - MALE_SIZE / 2, MALE_SIZE, MALE_SIZE, STROKE_COLOR, STROKE_WIDTH, color);
		label(ind);
	}

}
