package forms.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;
import play.i18n.Messages;
import truyen.common.bo.truyen.ChapterBo;
import truyen.common.util.TruyenUtils;

public class FormAddChapter {
    public String type;
    public String is_active;
    public String title, content;

    public int chapterType;
    public boolean isActive;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        title = !StringUtils.isBlank(this.title) ? this.title.trim() : null;
        content = !StringUtils.isBlank(this.content) ? this.content.trim() : null;
        if (StringUtils.isBlank(title)) {
            errors.add(new ValidationError("title", Messages.get("error.chapter.empty_title")));
        }
        if (StringUtils.isBlank(content)) {
            errors.add(new ValidationError("content", Messages.get("error.chapter.empty_content")));
        }
        try {
            chapterType = Integer.parseInt(type);
        } catch (Exception e) {
            chapterType = ChapterBo.TYPE_RAW;
        }
        isActive = TruyenUtils.toBoolean(is_active);

        return errors.isEmpty() ? null : errors;
    }
}
