package packing.payload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static java.lang.Math.max;

public class Container extends Box {

	private int stackWeight = 0;
	private int stackHeight = 0;
	private ArrayList<Level> levels = new ArrayList<>();

	public Container(Container container) {
		super(container.getName(), container.getWidth(), container.getDepth(), container.getHeight(), container.getWeight());
	}

	/**
	 * Construct new instance.
	 *
	 * @param dimension maximum size the container can contain
	 * @param weight maximum weight the container can hold
	 */
	public Container(Dimension dimension, int weight) {
		super(dimension.getName(), dimension.getWidth(), dimension.getDepth(), dimension.getHeight(), weight);
	}

	/**
	 * Construct new instance.
	 *
	 * @param w width
	 * @param d depth
	 * @param h height
	 * @param weight maximum weight the container can hold
	 */
	public Container(int w, int d, int h, int weight) {
		super(w, d, h, weight);
	}

	/**
	 * Construct new instance.
	 *
	 * @param name container name
	 * @param w width
	 * @param d depth
	 * @param h height
	 * @param weight maximum weight the container can hold
	 */
	public Container(String name, int w, int d, int h, int weight) {
		super(name, w, d, h, weight);
	}

	/**
	 * The 6 different possible rotations.
	 *
	 * It is sometimes useful to pass this list to the {@link LargestAreaFitFirstPackager}
	 * since it has a better chance to find a packaging than with a single container.
	 */
	public List<Container> rotations(){
		return rotationsStream().collect(Collectors.toList());
	}

	Stream<Container> rotationsStream() {
		return Stream.of(
				new Container(width, height, depth, weight),
				new Container(width, depth, height, weight),
				new Container(height, width, depth, weight),
				new Container(height, depth, width, weight),
				new Container(depth, height, width, weight),
				new Container(depth, width, height, weight));
	}

	public boolean add(Level element) {
		if(!levels.isEmpty()) {
			stackHeight += currentLevelStackHeight();
			stackWeight += currentLevelStackWeight();
		}

		return levels.add(element);
	}

	int getStackHeight() {
		return stackHeight + currentLevelStackHeight();
	}

	int getStackWeight() {
		return stackWeight + currentLevelStackWeight();
	}

	private int currentLevelStackHeight() {
		if(levels.isEmpty()) {
			return 0;
		}
		return levels.get(levels.size() - 1).getHeight();
	}

	private int currentLevelStackWeight() {
		if(levels.isEmpty()) {
			return 0;
		}
		return levels.get(levels.size() - 1).getWeight();
	}

	public void add(Placement placement) {
		levels.get(levels.size() - 1).add(placement);
	}

	void addLevel() {
		add(new Level());
	}

	Dimension getFreeSpace() {
		int remainder = height - getStackHeight();
		if(remainder < 0) {
			throw new IllegalArgumentException("Remaining free space is negative at " + remainder);
		}
		return new Dimension(width, depth, remainder);
	}

	int getFreeWeight() {
		int remainder = weight - getStackWeight();
		if(remainder < 0) {
			throw new IllegalArgumentException("Remaining weight is negative at " + remainder);
		}
		return remainder;
	}

	public List<Level> getLevels() {
		return levels;
	}

	public Placement get(int level, int placement) {
		return levels.get(level).get(placement);
	}

	// keep method for tests
	public void validateCurrentLevel() {
		levels.get(levels.size() - 1).validate();
	}

	public void clear() {
		levels.clear();
		stackHeight = 0;
		stackWeight = 0;
	}

	int getBoxCount() {
		int count = 0;
		for(Level level : levels) {
			count += level.size();
		}
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((levels == null) ? 0 : levels.hashCode());
		result = prime * result + stackHeight;
		result = prime * result + stackWeight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
		if (levels == null) {
			if (other.levels != null)
				return false;
		} else if (!levels.equals(other.levels))
			return false;
		if (stackHeight != other.stackHeight)
			return false;
		if (stackWeight != other.stackWeight)
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return "Container [stackWeight=" + stackWeight + ", stackHeight=" + stackHeight + ", levels=" + levels
				//+ ", weight=" + weight + ", width=" + width + ", depth=" + depth + ", height=" + height + ", volume="
				//+ volume + ", name=" + name + "]";
		String test = "";
		int lvl_ct = 0;
		for(Level lvls : levels)
		{
			if(lvl_ct==0)
			{
				test += "[{\"Level_"+lvl_ct+"\":[";
				int pl_ct = 0;
				for(Placement pm : lvls)
				{
					if(pl_ct == 0)
					{
						test += "{"+pm+"}";
					}
					else
					{
						test += ",{"+pm+"}";
					}
					pl_ct++;
				}
				test += "]}";
			}
			else
			{
				test += ",{\"Level_"+lvl_ct+"\":[";
				int pl_ct = 0;
				for(Placement pm : lvls)
				{
					if(pl_ct == 0)
					{
						test += "{"+pm+"}";
					}
					else
					{
						test += ",{"+pm+"}";
					}
					pl_ct++;
				}
				test += "]";
			}
			lvl_ct++;
		}
		test += "}]";
		//System.out.println("VJP :"+test);
		return "{\"Container\":{\"stackWeight\":\"" + stackWeight + "\", \"stackHeight\":\"" + stackHeight + "\", \"levels\":" + test
				+ ", \"weight\":\"" + weight + "\", \"width\":\"" + width + "\", \"depth\":\"" + depth + "\", \"height\":\"" + height + "\", \"volume\":\""
				+ volume + "\", \"name\":\"" + name + "\"}}";
	}



	public Dimension getUsedSpace() {
		Dimension maxBox = Dimension.EMPTY;
		int height = 0;
		for (Level level : levels) {
			maxBox = getUsedSpace(level, maxBox, height);
			height += level.getHeight();
		}
		return maxBox;
	}

	private Dimension getUsedSpace(Level level, Dimension maxBox, int height) {
		for (Placement placement : level) {
			maxBox = boundingBox(maxBox, getUsedSpace(placement, height));
		}
		return maxBox;
	}

	private Dimension getUsedSpace(Placement placement, int height) {
		final Box box = placement.getBox();
		final Space space = placement.getSpace();
		return new Dimension(
				space.getX() + box.getWidth(),
				space.getY() + box.getDepth(),
				height + box.getHeight());
	}

	private Dimension boundingBox(final Dimension b1, final Dimension b2) {
		return new Dimension(
				max(b1.getWidth(), b2.getWidth()),
				max(b1.getDepth(), b2.getDepth()),
				max(b1.getHeight(), b2.getHeight()));

	}
}
