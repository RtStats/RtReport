package forms.admin;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.ValidationError;
import play.i18n.Messages;
import truyen.common.bo.truyen.BookBo;
import truyen.common.bo.truyen.TruyenDao;

public class FormAddWorker {
    public String book_id, engine, url;
    public BookBo book;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        try {
            book = TruyenDao.getBook(Integer.parseInt(book_id));
        } catch (Exception e) {
            book = null;
        }
        if (book == null) {
            errors.add(new ValidationError("book_id", Messages.get("error.book.not_found")));
        }

        return errors.isEmpty() ? null : errors;
    }
}
