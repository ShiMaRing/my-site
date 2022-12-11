package com.xgs.controller.admin;

import com.xgs.constant.LogActions;
import com.xgs.constant.Types;
import com.xgs.controller.BaseController;
import com.xgs.dto.cond.ContentCond;
import com.xgs.dto.cond.MetaCond;
import com.xgs.exception.BusinessException;
import com.xgs.model.ContentDomain;
import com.xgs.model.MetaDomain;
import com.xgs.service.content.ContentService;
import com.xgs.service.log.LogService;
import com.xgs.service.meta.MetaService;
import com.xgs.utils.APIResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 文章管理
 * Created by winterchen on 2018/4/30.
 */
@Api("文章管理")
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = BusinessException.class)
public class ArticleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ContentService contentService;

    @Autowired
    private MetaService metaService;

    @Autowired
    private LogService logService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation("文章页")
    @GetMapping(value = "")
    public String index(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
            int page,
            @ApiParam(name = "limit", value = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "15")
            int limit
    ) {
        PageInfo<ContentDomain> articles = contentService.getArticlesByCond(new ContentCond(), page, limit);
        //add all to redis
        List<ContentDomain> list = articles.getList();
        for (ContentDomain contentDomain : list) {
            redisTemplate.opsForValue().set("article-" + contentDomain.getCid(), contentDomain);
        }
        request.setAttribute("articles", articles);
        return "admin/article_list";
    }


    @ApiOperation("发布文章页")
    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        MetaCond metaCond = new MetaCond();
        metaCond.setType(Types.CATEGORY.getType());
        List<MetaDomain> metas = metaService.getMetas(metaCond);
        request.setAttribute("categories", metas);
        return "admin/article_edit";
    }

    @ApiOperation("发布新文章")
    @PostMapping(value = "/publish")
    @ResponseBody
    public APIResponse publishArticle(
            HttpServletRequest request,
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @ApiParam(name = "tags", value = "标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @ApiParam(name = "categories", value = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
            Boolean allowComment
    ) {
        ContentDomain contentDomain = new ContentDomain();
        contentDomain.setTitle(title);
        contentDomain.setTitlePic(titlePic);
        contentDomain.setSlug(slug);
        contentDomain.setContent(content);
        contentDomain.setType(type);
        contentDomain.setStatus(status);
        contentDomain.setTags(type.equals(Types.ARTICLE.getType()) ? tags : null);
        //只允许博客文章有分类，防止作品被收入分类
        contentDomain.setCategories(type.equals(Types.ARTICLE.getType()) ? categories : null);
        contentDomain.setAllowComment(allowComment ? 1 : 0);

        contentService.addArticle(contentDomain);

        //添加到缓存
        redisTemplate.opsForValue().set("article-" + contentDomain.getCid(), contentDomain);

        return APIResponse.success();


    }

    @ApiOperation("文章编辑页")
    @GetMapping(value = "/{cid}")
    public String editArticle(
            @ApiParam(name = "cid", value = "文章编号", required = true)
            @PathVariable
            Integer cid,
            HttpServletRequest request
    ) {
        //search from redis first
        ContentDomain content;
        Boolean result = redisTemplate.hasKey("article-" + cid);
        if (Boolean.FALSE.equals(result)) {
            content = contentService.getArticleById(cid);
        } else {
            System.out.println("get article from redis");
            content = (ContentDomain) redisTemplate.opsForValue().get("article-" + cid);
        }

        request.setAttribute("contents", content);
        MetaCond metaCond = new MetaCond();
        metaCond.setType(Types.CATEGORY.getType());
        List<MetaDomain> categories = metaService.getMetas(metaCond);
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
    }

    @ApiOperation("编辑保存文章")
    @PostMapping("/modify")
    @ResponseBody
    public APIResponse modifyArticle(
            HttpServletRequest request,
            @ApiParam(name = "cid", value = "文章主键", required = true)
            @RequestParam(name = "cid", required = true)
            Integer cid,
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @ApiParam(name = "tags", value = "标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @ApiParam(name = "categories", value = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
            Boolean allowComment
    ) {
        ContentDomain contentDomain = new ContentDomain();
        contentDomain.setCid(cid);
        contentDomain.setTitle(title);
        contentDomain.setTitlePic(titlePic);
        contentDomain.setSlug(slug);
        contentDomain.setContent(content);
        contentDomain.setType(type);
        contentDomain.setStatus(status);
        contentDomain.setTags(tags);
        contentDomain.setCategories(categories);
        contentDomain.setAllowComment(allowComment ? 1 : 0);

        contentService.updateArticleById(contentDomain);
        //添加到缓存
        redisTemplate.opsForValue().set("article-" + contentDomain.getCid(), contentDomain);
        return APIResponse.success();
    }

    @ApiOperation("删除文章")
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteArticle(
            @ApiParam(name = "cid", value = "文章主键", required = true)
            @RequestParam(name = "cid", required = true)
            Integer cid,
            HttpServletRequest request
    ) {
        contentService.deleteArticleById(cid);
        redisTemplate.delete("article-" + cid);
        logService.addLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(), this.getUid(request));
        return APIResponse.success();
    }
}
