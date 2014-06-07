package truyen.common.bo.truyen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import truyen.common.Constants;
import truyen.common.bo.CounterDao;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;

public class TruyenDao extends BaseMysqlDao {

    public final static String TABLE_AUTHOR = Constants.APP_ID + "_author";
    public final static String TABLE_CATEGORY = Constants.APP_ID + "_category";
    public final static String TABLE_BOOK = Constants.APP_ID + "_book";

    private final static AuthorBo[] EMPTY_ARR_AUTHOR_BO = new AuthorBo[0];
    private final static CategoryBo[] EMPTY_ARR_CATEGORY_BO = new CategoryBo[0];
    private final static BookBo[] EMPTY_ARR_BOOK_BO = new BookBo[0];

    /*----------------------------------------------------------------------*/
    private static String cacheKeyAuthor(int id) {
        return Constants.CACHE_PREFIX + "_AUTHOR_" + id;
    }

    private static String cacheKey(AuthorBo author) {
        return cacheKeyAuthor(author.getId());
    }

    private static String cacheKeyAllAuthors() {
        return Constants.CACHE_PREFIX + "_ALL_AUTHORS";
    }

    private static void invalidate(AuthorBo author) {
        removeFromCache(cacheKey(author));
        removeFromCache(cacheKeyAllAuthors());
    }

    /*--------------------*/

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

    /*--------------------*/

    private static String cacheKeyBook(int id) {
        return Constants.CACHE_PREFIX + "_BOOK_" + id;
    }

    private static String cacheKey(BookBo book) {
        return cacheKeyBook(book.getId());
    }

    private static String cacheKeyAllBooks() {
        return Constants.CACHE_PREFIX + "_ALLBOOKS";
    }

    private static String cacheKeyAllBooks(CategoryBo cat) {
        return Constants.CACHE_PREFIX + "_BOOKS_" + cat.getId();
    }

    private static void invalidate(BookBo book) {
        removeFromCache(cacheKey(book));
        removeFromCache(cacheKeyAllBooks());
        CategoryBo cat = book.getCategory();
        if (cat != null) {
            removeFromCache(cacheKeyAllBooks(cat));
        }
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new author.
     * 
     * @param author
     * @return
     */
    public static AuthorBo create(final AuthorBo author) {
        if (author.getId() < 1) {
            author.setId((int) CounterDao.nextId(Constants.COUNTER_AUTHOR_ID));
        }
        final String[] COLUMNS = new String[] { AuthorBo.COL_ID[0], AuthorBo.COL_NAME[0],
                AuthorBo.COL_NUM_BOOKS[0], AuthorBo.COL_INFO[0] };
        final Object[] VALUES = new Object[] { author.getId(), author.getName(),
                author.getNumBooks(), author.getInfo() };
        insertIgnore(TABLE_AUTHOR, COLUMNS, VALUES);
        invalidate(author);
        return (AuthorBo) author.markClean();
    }

    /**
     * Deletes an existing author.
     * 
     * @param author
     */
    public static void delete(AuthorBo author) {
        final String[] COLUMNS = new String[] { AuthorBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { author.getId() };
        delete(TABLE_AUTHOR, COLUMNS, VALUES);
        invalidate(author);
    }

    /**
     * Gets an author by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static AuthorBo getAuthor(int id) {
        final String CACHE_KEY = cacheKeyAuthor(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { AuthorBo.COL_ID, AuthorBo.COL_NAME,
                    AuthorBo.COL_NUM_BOOKS, AuthorBo.COL_INFO };
            final String whereClause = AuthorBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_AUTHOR, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (AuthorBo) new AuthorBo().fromMap(dbRow) : null;
    }

    /**
     * Gets all authors as a list.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static AuthorBo[] getAllAuthors() {
        final String CACHE_KEY = cacheKeyAllAuthors();
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String SQL = MessageFormat.format("SELECT {1} AS {2} FROM {0} ORDER BY {3} DESC",
                    TABLE_AUTHOR, AuthorBo.COL_ID[0], AuthorBo.COL_ID[1], AuthorBo.COL_ID[0]);
            dbRows = select(SQL, null);
            putToCache(CACHE_KEY, dbRows);
        }
        List<AuthorBo> result = new ArrayList<AuthorBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, AuthorBo.COL_ID[1], int.class);
                AuthorBo author = getAuthor(id);
                if (author != null) {
                    result.add(author);
                }
            }
        }
        return result.toArray(EMPTY_ARR_AUTHOR_BO);
    }

    /**
     * Updates an existing author.
     * 
     * @param author
     * @return
     */
    public static AuthorBo update(AuthorBo author) {
        if (author.isDirty()) {
            final String CACHE_KEY = cacheKey(author);
            final String[] COLUMNS = new String[] { AuthorBo.COL_NAME[0], AuthorBo.COL_INFO[0] };
            final Object[] VALUES = new Object[] { author.getName(), author.getInfo() };
            final String[] WHERE_COLUMNS = new String[] { AuthorBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { author.getId() };
            update(TABLE_AUTHOR, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = author.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (AuthorBo) author.markClean();
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
                CategoryBo.COL_NUM_BOOKS[0], CategoryBo.COL_TITLE[0], CategoryBo.COL_SUMMARY[0] };
        final Object[] VALUES = new Object[] { category.getId(), category.getPosition(),
                category.getNumBooks(), category.getTitle(), category.getSummary() };
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
                    CategoryBo.COL_NUM_BOOKS, CategoryBo.COL_TITLE, CategoryBo.COL_SUMMARY };
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

    /**
     * Moves a category up the list.
     * 
     * @param cat
     */
    public static void moveUp(CategoryBo cat) {
        CategoryBo[] allCats = TruyenDao.getAllCategories();
        for (int i = 1; i < allCats.length; i++) {
            if (allCats[i].getId() == cat.getId()) {
                int temp = allCats[i].getPosition();
                allCats[i].setPosition(temp + 1);
                allCats[i - 1].setPosition(temp);
                TruyenDao.update(allCats[i]);
                TruyenDao.update(allCats[i - 1]);
                removeFromCache(cacheKeyAllCategories());
                break;
            }
        }
    }

    /**
     * Moves a category down the list.
     * 
     * @param cat
     */
    public static void moveDown(CategoryBo cat) {
        CategoryBo[] allCats = TruyenDao.getAllCategories();
        for (int i = 0, n = allCats.length - 1; i < n; i++) {
            if (allCats[i].getId() == cat.getId()) {
                int temp = allCats[i].getPosition();
                allCats[i].setPosition(temp - 1);
                allCats[i + 1].setPosition(temp);
                TruyenDao.update(allCats[i]);
                TruyenDao.update(allCats[i + 1]);
                removeFromCache(cacheKeyAllCategories());
                break;
            }
        }
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new book.
     * 
     * @param book
     * @return
     */
    public static BookBo create(final BookBo book) {
        if (book.getId() < 1) {
            book.setId((int) CounterDao.nextId(Constants.COUNTER_BOOK_ID));
        }
        final String[] COLUMNS = new String[] { BookBo.COL_ID[0], BookBo.COL_STATUS[0],
                BookBo.COL_IS_PUBLISHED[0], BookBo.COL_NUM_CHAPTERS[0], BookBo.COL_CATEGORY_ID[0],
                BookBo.COL_AUTHOR_ID[0], BookBo.COL_TITLE[0], BookBo.COL_SUMMARY[0],
                BookBo.COL_AVATAR[0], BookBo.COL_TIMESTAMP_CREATE[0],
                BookBo.COL_TIMESTAMP_UPDATE[0] };
        final Object[] VALUES = new Object[] { book.getId(), book.getStatus(),
                book.isPublished() ? Constants.INT_1 : Constants.INT_0, book.getNumChapters(),
                book.getCategoryId(), book.getAuthorId(), book.getTitle(), book.getSummary(),
                book.getAvatar(), book.getTimestampCreate(), book.getTimestampUpdate() };
        insertIgnore(TABLE_BOOK, COLUMNS, VALUES);
        invalidate(book);
        return (BookBo) book.markClean();
    }

    /**
     * Deletes an existing book.
     * 
     * @param book
     */
    public static void delete(BookBo book) {
        final String[] COLUMNS = new String[] { BookBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { book.getId() };
        delete(TABLE_BOOK, COLUMNS, VALUES);
        invalidate(book);
    }

    /**
     * Gets a book by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static BookBo getBook(int id) {
        final String CACHE_KEY = cacheKeyBook(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        dbRow = null;
        if (dbRow == null) {
            final String[][] columns = { BookBo.COL_ID, BookBo.COL_STATUS, BookBo.COL_IS_PUBLISHED,
                    BookBo.COL_NUM_CHAPTERS, BookBo.COL_CATEGORY_ID, BookBo.COL_AUTHOR_ID,
                    BookBo.COL_TITLE, BookBo.COL_SUMMARY, BookBo.COL_AVATAR,
                    BookBo.COL_TIMESTAMP_CREATE, BookBo.COL_TIMESTAMP_UPDATE };
            final String whereClause = BookBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_BOOK, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (BookBo) new BookBo().fromMap(dbRow) : null;
    }

    /**
     * Gets all books of category.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static BookBo[] getAllBooksForCategory(CategoryBo cat) {
        final String CACHE_KEY = cacheKeyAllBooks(cat);
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String SQL = MessageFormat
                    .format("SELECT {1} AS {2} FROM {0} WHERE {3}=? ORDER BY {4} DESC", TABLE_BOOK,
                            BookBo.COL_ID[0], BookBo.COL_ID[1], BookBo.COL_CATEGORY_ID[0],
                            BookBo.COL_ID[0]);
            dbRows = select(SQL, null);
            putToCache(CACHE_KEY, dbRows);
        }
        List<BookBo> result = new ArrayList<BookBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, BookBo.COL_ID[1], int.class);
                BookBo book = getBook(id);
                if (book != null) {
                    result.add(book);
                }
            }
        }
        return result.toArray(EMPTY_ARR_BOOK_BO);
    }

    /**
     * Gets all books.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static BookBo[] getAllBooks() {
        final String CACHE_KEY = cacheKeyAllBooks();
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String SQL = MessageFormat.format("SELECT {1} AS {2} FROM {0} ORDER BY {3} DESC",
                    TABLE_BOOK, BookBo.COL_ID[0], BookBo.COL_ID[1], BookBo.COL_ID[0]);
            dbRows = select(SQL, null);
            putToCache(CACHE_KEY, dbRows);
        }
        List<BookBo> result = new ArrayList<BookBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, BookBo.COL_ID[1], int.class);
                BookBo book = getBook(id);
                if (book != null) {
                    result.add(book);
                }
            }
        }
        return result.toArray(EMPTY_ARR_BOOK_BO);
    }

    /**
     * Updates an existing book.
     * 
     * @param book
     * @return
     */
    public static BookBo update(BookBo book) {
        if (book.isDirty()) {
            final String CACHE_KEY = cacheKey(book);
            final String[] COLUMNS = new String[] { BookBo.COL_STATUS[0],
                    BookBo.COL_IS_PUBLISHED[0], BookBo.COL_CATEGORY_ID[0], BookBo.COL_AUTHOR_ID[0],
                    BookBo.COL_TITLE[0], BookBo.COL_SUMMARY[0], BookBo.COL_AVATAR[0] };
            final Object[] VALUES = new Object[] { book.getStatus(),
                    book.isPublished() ? Constants.INT_1 : Constants.INT_0, book.getCategoryId(),
                    book.getAuthorId(), book.getTitle(), book.getSummary(), book.getAvatar() };
            final String[] WHERE_COLUMNS = new String[] { BookBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { book.getId() };
            update(TABLE_BOOK, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = book.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (BookBo) book.markClean();
    }

    /**
     * Updates a book's author.
     * 
     * @param book
     * @param author
     * @return
     */
    public static BookBo update(BookBo book, AuthorBo author) {
        book.setAuthor(author);
        return update(book);
    }

    /**
     * Updates a book's category.
     * 
     * @param book
     * @param category
     * @return
     */
    public static BookBo update(BookBo book, CategoryBo category) {
        book.setCategory(category);
        return update(book);
    }

    /*----------------------------------------------------------------------*/
}
