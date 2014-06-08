package truyen.common.bo.worker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import truyen.common.Constants;
import truyen.common.bo.CounterDao;
import truyen.common.bo.truyen.AuthorBo;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;

public class WorkerDao extends BaseMysqlDao {

    public final static String TABLE_WORKER = Constants.APP_ID + "_worker";

    private final static WorkerBo[] EMPTY_ARR_WORKER_BO = new WorkerBo[0];

    /*----------------------------------------------------------------------*/
    private static String cacheKeyWorker(int id) {
        return Constants.CACHE_PREFIX + "_WORKER_" + id;
    }

    private static String cacheKey(WorkerBo worker) {
        return cacheKeyWorker(worker.getId());
    }

    private static String cacheKeyAllWorkers() {
        return Constants.CACHE_PREFIX + "_ALL_WORKERS";
    }

    private static void invalidate(WorkerBo worker) {
        removeFromCache(cacheKey(worker));
        removeFromCache(cacheKeyAllWorkers());
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new worker.
     * 
     * @param worker
     * @return
     */
    public static WorkerBo create(final WorkerBo worker) {
        if (worker.getId() < 1) {
            worker.setId((int) CounterDao.nextId(Constants.COUNTER_WORKER_ID));
        }
        final String[] COLUMNS = new String[] { WorkerBo.COL_ID[0], WorkerBo.COL_BOOK_ID[0],
                WorkerBo.COL_ENGINE[0], WorkerBo.COL_URL[0], WorkerBo.COL_STATUS[0],
                WorkerBo.COL_LAST_TIMESTAME[0], WorkerBo.COL_LAST_STATUS[0] };
        final Object[] VALUES = new Object[] { worker.getId(), worker.getBookId(),
                worker.getEngine(), worker.getUrl(), worker.getStatus(), worker.getLastTimestamp(),
                worker.getLastStatus() };
        insertIgnore(TABLE_WORKER, COLUMNS, VALUES);
        invalidate(worker);
        return (WorkerBo) worker.markClean();
    }

    /**
     * Deletes an existing worker.
     * 
     * @param worker
     */
    public static void delete(WorkerBo worker) {
        final String[] COLUMNS = new String[] { WorkerBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { worker.getId() };
        delete(TABLE_WORKER, COLUMNS, VALUES);
        invalidate(worker);
    }

    /**
     * Gets a worker by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static WorkerBo getWorker(int id) {
        final String CACHE_KEY = cacheKeyWorker(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { WorkerBo.COL_ID, WorkerBo.COL_BOOK_ID,
                    WorkerBo.COL_ENGINE, WorkerBo.COL_URL, WorkerBo.COL_STATUS,
                    WorkerBo.COL_LAST_TIMESTAME, WorkerBo.COL_LAST_STATUS };
            final String whereClause = WorkerBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_WORKER, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (WorkerBo) new WorkerBo().fromMap(dbRow) : null;
    }

    /**
     * Gets all workers as a list.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static WorkerBo[] getAllWorkers() {
        final String CACHE_KEY = cacheKeyAllWorkers();
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String SQL = MessageFormat.format("SELECT {1} AS {2} FROM {0} ORDER BY {3} DESC",
                    TABLE_WORKER, WorkerBo.COL_ID[0], WorkerBo.COL_ID[1], AuthorBo.COL_ID[0]);
            dbRows = select(SQL, null);
            putToCache(CACHE_KEY, dbRows);
        }
        List<WorkerBo> result = new ArrayList<WorkerBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, WorkerBo.COL_ID[1], int.class);
                WorkerBo worker = getWorker(id);
                if (worker != null) {
                    result.add(worker);
                }
            }
        }
        return result.toArray(EMPTY_ARR_WORKER_BO);
    }

    /**
     * Updates an existing worker.
     * 
     * @param worker
     * @return
     */
    public static WorkerBo update(WorkerBo worker) {
        if (worker.isDirty()) {
            final String CACHE_KEY = cacheKey(worker);
            final String[] COLUMNS = new String[] { WorkerBo.COL_STATUS[0],
                    WorkerBo.COL_LAST_STATUS[0], WorkerBo.COL_LAST_TIMESTAME[0] };
            final Object[] VALUES = new Object[] { worker.getStatus(), worker.getLastStatus(),
                    worker.getLastTimestamp() };
            final String[] WHERE_COLUMNS = new String[] { WorkerBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { worker.getId() };
            update(TABLE_WORKER, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = worker.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (WorkerBo) worker.markClean();
    }

    /*----------------------------------------------------------------------*/
}
