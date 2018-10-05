package partmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

public class Component
{
	private String name;
	private String desc;
	private String footprint;
	private int qty;
        private String iconURL;
	private static List<Component> list = new ArrayList();
	private static final String[] PACKAGE_LIST = {"BGA","DIP","LCC","SOIC",
            "SO\\d+","SOP","SOT","TO-?\\d+","QFN","QFP"
            };
	
	public enum Footprint{SOIC,TQFP}
	
	public Component(String name)
	{
		this.name = name;
	}
	
	public Component(String name, String desc, String footprint)
	{
		this.name = name;
		this.desc = desc;
		this.footprint = footprint;
	}
	
	public Component(Object object)
	{
            Object obj[] = (Object[]) object;
            this.name = (String) obj[0];
            this.desc = (String) obj[1];
            this.footprint = (String) obj[2];
            this.qty = (int) obj[3];
            if (obj.length > 4)
                this.iconURL = (String) obj[4];
	}
        
	public Component(JSONObject jo) throws JSONException
	{
            name = jo.get("mpn").toString();
            desc = jo.get("short_description").toString();
//            JSONArray img = jo.getJSONArray("imagesets");
//            for (int i=0; i<img.length(); i++)
//            {
//                JSONObject item = (JSONObject) img.getJSONObject(i).get("small_image");
//            }
            for (String pack : PACKAGE_LIST) {
                String descCaps = desc.toUpperCase();
                Pattern packagePattern = Pattern.compile("(?:(\\d+)-)?(\\w*)?("+pack+")(?:(?:\\s|-)?(\\d+))?");
                Matcher m = packagePattern.matcher(descCaps);
                if (m.find())
                {
                    String pkg = m.group(3);
                    String prefixPinCount = m.group(1);
                    String suffixPinCount = m.group(4);
                    String result = (m.group(2) == null) ? pkg : m.group(2)+pkg;
                    
                    if (prefixPinCount != null)
                        result = result.concat("-"+prefixPinCount);
                    else if (suffixPinCount != null)
                        result = result.concat("-"+suffixPinCount);
                    else
//                    if (!result.matches(".*//d+.*"))
                    {
                        Pattern pinPattern = Pattern.compile("(\\d+)-?(?:PIN|LEAD)");
                        m = pinPattern.matcher(descCaps);
                        if (m.find())
                        {
                            result = result.concat("-"+m.group(1));
                        }
                    }
                    footprint = result;
                    break;
                }
            }
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public String getFootprint()
	{
		return footprint;
	}
	
	public int getQuantity()
	{
		return qty;
	}
        
        public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDescription(String desc)
	{
		this.desc = desc;
	}
	
	public void setFootprint(String pkg)
	{
		this.footprint = pkg;
	}
	
	public void setStock(int qty)
	{
		this.qty = qty;
	}
        /************************************************************/
        /* Static Methods */
	
	public static void addComponent(Component c)
	{
		list.add(c);
	}
	
	public static List<Component> getList()
	{
		return list;
	}
	
	public static Object[] getComponentObject(int i)
	{
		Object[] obj = new Object[4];
		obj[0] = list.get(i).name;
		obj[1] = list.get(i).desc;
		obj[2] = list.get(i).footprint;
		obj[3] = list.get(i).qty;
		return obj;
	}
	
        public static Component getComponentByName(String name)
        {
            for (Component cmp : list)
            {
                if (cmp.name.equals(name))
                {
                    return cmp;
                }
            }
            return null;
        }
}
