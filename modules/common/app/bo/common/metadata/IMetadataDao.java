package bo.common.metadata;

import java.util.List;

public interface IMetadataDao {
    /**
     * Gets all available tags.
     * 
     * @return
     */
    public List<String> getAllTags();

    /**
     * Gets all available counter names.
     * 
     * @return
     */
    public List<String> getAllCounters();

    /**
     * Gets all counters that have a specified tag.
     * 
     * @param tag
     * @return
     */
    public List<String> getCountersForTag(String tag);
}
