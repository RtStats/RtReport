package bo.common.metadata;

import java.util.ArrayList;
import java.util.List;

import bo.common.cassandra.BaseCassandraDao;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraMetadataDao extends BaseCassandraDao implements IMetadataDao {

    private PreparedStatement pGetAllList, pGetCountersForTag;

    /**
     * {@inheritDoc}
     */
    @Override
    public CassandraMetadataDao init() {
        super.init();

        // init statements
        Session session = getSession();
        pGetAllList = session.prepare("SELECT * FROM tsc_all_list WHERE n=?");
        pGetCountersForTag = session.prepare("SELECT * FROM tsc_tag_counter WHERE t=?");

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /*--------------------------------------------------------------------------------*/
    private final static String LIST_TAGS = "all_tags";
    private final static String LIST_COUNTERS = "all_counters";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllCounters() {
        List<String> result = new ArrayList<String>();
        ResultSet rs = execute(getSession(), pGetAllList, LIST_COUNTERS);
        List<Row> rows = rs.all();
        for (Row row : rows) {
            result.add(row.getString("k"));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllTags() {
        List<String> result = new ArrayList<String>();
        ResultSet rs = execute(getSession(), pGetAllList, LIST_TAGS);
        List<Row> rows = rs.all();
        for (Row row : rows) {
            result.add(row.getString("k"));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCountersForTag(String tag) {
        List<String> result = new ArrayList<String>();
        ResultSet rs = execute(getSession(), pGetCountersForTag, tag);
        List<Row> rows = rs.all();
        for (Row row : rows) {
            result.add(row.getString("c"));
        }
        return result;
    }
}
