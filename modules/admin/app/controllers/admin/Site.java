package controllers.admin;

import play.api.templates.Html;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import truyen.common.Constants;
import truyen.common.bo.ConfDao;
import truyen.common.util.FormUtils;

import compisitions.admin.AuthRequired;

import controllers.common.BaseController;
import forms.admin.FormSiteConfig;

@AuthRequired(userGroups = { Constants.USER_GROUP_ADMINISTRATOR, Constants.USER_GROUP_MODERATOR })
public class Site extends BaseController {

    public final static String VIEW_SITE = SECTION + "site";

    /*
     * Handles GET:/siteConfig
     */
    public static Promise<Result> siteConfig() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_SITE);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles POST:/siteConfigFacebook
     */
    public static Promise<Result> siteConfigFacebookSubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormSiteConfig> form = Form.form(FormSiteConfig.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_SITE,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + FormUtils.joinFormErrorMessages(form));
                } else {
                    FormSiteConfig formData = form.get();
                    ConfDao.createOrUpdateConf(Constants.CONFIG_FACEBOOK_APP_ID, formData.fb_appid);
                    ConfDao.createOrUpdateConf(Constants.CONFIG_FACEBOOK_APP_SCOPE,
                            formData.fb_appscope);
                    flash(VIEW_SITE, Messages.get("msg.site_config.done"));
                }
                return redirect(controllers.admin.routes.Site.siteConfig().url());
            }
        });
        return promise;
    }

    /*
     * Handles POST:/siteConfigWebsite
     */
    public static Promise<Result> siteConfigWebsiteSubmit() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Form<FormSiteConfig> form = Form.form(FormSiteConfig.class).bindFromRequest();
                if (form.hasErrors()) {
                    flash(VIEW_SITE,
                            Constants.FLASH_MSG_PREFIX_ERROR
                                    + FormUtils.joinFormErrorMessages(form));
                } else {
                    FormSiteConfig formData = form.get();
                    ConfDao.createOrUpdateConf(Constants.CONFIG_SITE_NAME, formData.site_name);
                    ConfDao.createOrUpdateConf(Constants.CONFIG_SITE_TITLE, formData.site_title);
                    ConfDao.createOrUpdateConf(Constants.CONFIG_SITE_KEYWORDS,
                            formData.site_keywords);
                    ConfDao.createOrUpdateConf(Constants.CONFIG_SITE_DESCRTIPTION,
                            formData.site_description);
                    flash(VIEW_SITE, Messages.get("msg.site_config.done"));
                }
                return redirect(controllers.admin.routes.Site.siteConfig().url());
            }
        });
        return promise;
    }
}
