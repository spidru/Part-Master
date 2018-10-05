package partmaster;

import java.util.ArrayList;
import java.util.List;

public class PartSearch
	implements Runnable
{
	private volatile boolean interrupted;
	private final String term;
	private List<String> searchResults;
        private List<Component> foundPartsList;
        
//        public PartSearch() {}
	
	public PartSearch(String term)
	{
		this.term = term;
                interrupted = false;
	}
        
	@Override
	public void run()
	{
            searchResults = searchFor(term);
	}
	
	public List<String> searchFor(String key)
	{
            foundPartsList = Octopart.searchParts(key);
            List<String> stringList = new ArrayList();
            for (int i=0; i<foundPartsList.size();i++)
            {
                stringList.add(foundPartsList.get(i).getName());
            }
            return stringList;
	}
	
	public List<String> getSearchResults()
	{
            return searchResults;
	}
        
        public List<Component> getFoundComponents()
        {
            return foundPartsList;
        }
        
        public Component getFoundComponentByName(String name)
        {
            for (int i=0; i<foundPartsList.size(); i++)
            {
                if (foundPartsList.get(i).getName().equals(name))
                    return foundPartsList.get(i);
            }
            return null;
        }
        
        public void kill()
        {
            interrupted = true;
        }
        
        public boolean isRunning()
        {
            return interrupted;
        }
}
