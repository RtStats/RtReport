package com.litha.truyen.common.bo.truyen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;
import com.litha.truyen.common.Constants;
import com.litha.truyen.common.bo.CounterDao;

public class TruyenDao extends BaseMysqlDao {

    public final static String TABLE_CATEGORY = Constants.APP_ID + "_category";

    private final static CategoryBo[] EMPTY_ARR_CATEGORY_BO = new CategoryBo[0];

    /*----------------------------------------------------------------------*/
    private static String cacheKeyCategory(int id) {
        return Constants.CACHE_PREFIX + "_CATEGORY_" + id;
    }

    private static String cacheKey(CategoryBo category) {
        return cacheKeyCategory(category.getId());
    }

    private static String cacheKeyAllCategories() {
        return Constants.CACHE_PREFIX + "_ALL_CATEGORIES";
    }

    private static void invalidate(CategoryBo category) {
        removeFromCache(cacheKey(category));
        removeFromCache(cacheKeyAllCategories());
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new category.
     * 
     * @param category
     * @return
     */
    public static CategoryBo create(final CategoryBo category) {
        if (category.getId() < 1) {
            category.setId((int) CounterDao.nextId(Constants.COUNTER_CATEGORY_ID));
        }
        if (category.getPosition() < 1) {
            category.setPosition((int) (System.currentTimeMillis() / 1000));
        }
        final String[] COLUMNS = new String[] { CategoryBo.COL_ID[0], CategoryBo.COL_POSITION[0],
                CategoryBo.COL_NUM_STORIES[0], CategoryBo.COL_TITLE[0], CategoryBo.COL_SUMMARY[0] };
        final Object[] VALUES = new Object[] { category.getId(), category.getPosition(),
                category.getNumStories(), category.getTitle(), category.getSummary() };
        insertIgnore(TABLE_CATEGORY, COLUMNS, VALUES);
        invalidate(category);
        return (CategoryBo) category.markClean();
    }

    /**
     * Deletes an existing category.
     * 
     * @param category
     */
    public static void delete(CategoryBo category) {
        final String[] COLUMNS = new String[] { CategoryBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { category.getId() };
        delete(TABLE_CATEGORY, COLUMNS, VALUES);
        invalidate(category);
    }

    /**
     * Gets a category by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static CategoryBo getCategory(int id) {
        final String CACHE_KEY = cacheKeyCategory(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { CategoryBo.COL_ID, CategoryBo.COL_POSITION,
                    CategoryBo.COL_NUM_STORIES, CategoryBo.COL_TITLE, CategoryBo.COL_SUMMARY };
            final String whereClause = CategoryBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_CATEGORY, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (CategoryBo) new CategoryBo().fromMap(dbRow) : null;
    }

    /**
     * Gets all categories as a list.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static CategoryBo[] getAllCategories() {
        final String CACHE_KEY = cacheKeyAllCategories();
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String SQL = MessageFormat.format("SELECT {1} AS {2} FROM {0} ORDER BY {3} DESC",
                    TABLE_CATEGORY, CategoryBo.COL_ID[0], CategoryBo.COL_ID[1],
                    CategoryBo.COL_POSITION[0]);
            dbRows = select(SQL, null);
            putToCache(CACHE_KEY, dbRows);
        }
        List<CategoryBo> result = new ArrayList<CategoryBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, CategoryBo.COL_ID[1], int.class);
                CategoryBo category = getCategory(id);
                if (category != null) {
                    result.add(category);
                }
            }
        }
        return result.toArray(EMPTY_ARR_CATEGORY_BO);
    }

    /**
     * Updates an existing category.
     * 
     * @param category
     * @return
     */
    public static CategoryBo update(CategoryBo category) {
        if (category.isDirty()) {
            final String CACHE_KEY = cacheKey(category);
            final String[] COLUMNS = new String[] { CategoryBo.COL_POSITION[0],
                    CategoryBo.COL_TITLE[0], CategoryBo.COL_SUMMARY[0] };
            final Object[] VALUES = new Object[] { category.getPosition(), category.getTitle(),
                    category.getSummary() };
            final String[] WHERE_COLUMNS = new String[] { CategoryBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { category.getId() };
            update(TABLE_CATEGORY, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = category.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (CategoryBo) category.markClean();
    }

    /*----------------------------------------------------------------------*/
}
