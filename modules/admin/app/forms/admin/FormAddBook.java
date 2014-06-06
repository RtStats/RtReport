package forms.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;
import play.i18n.Messages;
import truyen.common.bo.truyen.AuthorBo;
import truyen.common.bo.truyen.CategoryBo;
import truyen.common.bo.truyen.TruyenDao;
import truyen.common.util.TruyenUtils;

public class FormAddBook {
    public String title, summary, avatar;
    public String author_id, category_id;
    public String status, is_published;
    public AuthorBo author;
    public CategoryBo category;
    public boolean isPublished;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        title = !StringUtils.isBlank(this.title) ? this.title.trim() : null;
        summary = !StringUtils.isBlank(this.summary) ? this.summary.trim() : null;
        if (StringUtils.isBlank(title)) {
            errors.add(new ValidationError("title", Messages.get("error.book.empty_title")));
        }
        try {
            author = TruyenDao.getAuthor(Integer.parseInt(author_id));
        } catch (Exception e) {
            author = null;
        }
        try {
            category = TruyenDao.getCategory(Integer.parseInt(category_id));
        } catch (Exception e) {
            category = null;
        }
        isPublished = TruyenUtils.toBoolean(is_published);

        return errors.isEmpty() ? null : errors;
    }
}
