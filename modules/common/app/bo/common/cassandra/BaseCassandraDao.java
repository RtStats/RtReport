package bo.common.cassandra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import play.Logger;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class BaseCassandraDao {
    private List<String> hosts = new ArrayList<String>();
    private String keyspace;
    private int port = 9042;
    private Cluster cluster;
    private Session session;

    public BaseCassandraDao addHost(String host) {
        hosts.add(host);
        return this;
    }

    protected String getHost() {
        return hosts.size() > 0 ? hosts.get(0) : null;
    }

    public BaseCassandraDao setHost(String host) {
        hosts.clear();
        hosts.add(host);
        return this;
    }

    protected Collection<String> getHosts() {
        return this.hosts;
    }

    public BaseCassandraDao setHosts(Collection<String> hosts) {
        this.hosts.clear();
        if (hosts != null) {
            this.hosts.addAll(hosts);
        }
        return this;
    }

    public BaseCassandraDao setHosts(String[] hosts) {
        this.hosts.clear();
        if (hosts != null) {
            for (String host : hosts) {
                this.hosts.add(host);
            }
        }
        return this;
    }

    protected int getPort() {
        return port;
    }

    public BaseCassandraDao setPort(int port) {
        this.port = port;
        return this;
    }

    protected String getKeyspace() {
        return keyspace;
    }

    public BaseCassandraDao setKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    protected Session getSession() {
        return session;
    }

    protected Cluster getCluster() {
        return cluster;
    }

    public BaseCassandraDao init() {
        if (cluster == null) {
            cluster = Cluster.builder()
                    .addContactPoints(hosts.toArray(ArrayUtils.EMPTY_STRING_ARRAY)).withPort(port)
                    .build();
        }

        // init connection
        session = cluster.connect(keyspace);

        return this;
    }

    public void destroy() {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            } finally {
                session = null;
            }
        }

        if (cluster != null) {
            try {
                cluster.close();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            } finally {
                cluster = null;
            }
        }
    }

    /*----------------------------------------------------------------------*/
    /**
     * Executes a non-SELECT query.
     * 
     * @param session
     * @param cql
     * @param bindValues
     */
    protected void executeNonSelect(Session session, String cql, Object... bindValues) {
        executeNonSelect(session, session.prepare(cql), bindValues);
    }

    /**
     * Executes a non-SELECT query.
     * 
     * @param session
     * @param stm
     * @param bindValues
     */
    protected void executeNonSelect(Session session, PreparedStatement stm, Object... bindValues) {
        BoundStatement bstm = stm.bind();
        if (bindValues != null && bindValues.length > 0) {
            bstm.bind(bindValues);
        }
        session.execute(bstm);
    }

    /**
     * Executes a SELECT query and returns results.
     * 
     * @param session
     * @param cql
     * @param bindValues
     * @return
     */
    protected ResultSet execute(Session session, String cql, Object... bindValues) {
        return execute(session, session.prepare(cql), bindValues);
    }

    /**
     * Executes a SELECT query and returns results.
     * 
     * @param session
     * @param stm
     * @param bindValues
     * @return
     */
    protected ResultSet execute(Session session, PreparedStatement stm, Object... bindValues) {
        BoundStatement bstm = stm.bind();
        if (bindValues != null && bindValues.length > 0) {
            bstm.bind(bindValues);
        }
        return session.execute(bstm);
    }

    /**
     * Executes a SELECT query and returns just one row.
     * 
     * @param session
     * @param cql
     * @param bindValues
     * @return
     */
    protected Row executeOne(Session session, String cql, Object... bindValues) {
        return executeOne(session, session.prepare(cql), bindValues);
    }

    /**
     * Executes a SELECT query and returns just one row.
     * 
     * @param session
     * @param stm
     * @param bindValues
     * @return
     */
    protected Row executeOne(Session session, PreparedStatement stm, Object... bindValues) {
        ResultSet rs = execute(session, stm, bindValues);
        return rs != null ? rs.one() : null;
    }
}
