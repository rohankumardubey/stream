package com.streamxhub.flink.monitor.core.controller;

import com.streamxhub.flink.monitor.base.controller.BaseController;
import com.streamxhub.flink.monitor.base.domain.RestRequest;
import com.streamxhub.flink.monitor.base.domain.RestResponse;
import com.streamxhub.flink.monitor.core.entity.Project;
import com.streamxhub.flink.monitor.core.service.ProjectService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author benjobs
 */
@Slf4j
@Validated
@RestController
@RequestMapping("flink/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("create")
    public RestResponse create(Project project) {
        return projectService.create(project);
    }

    @PostMapping("build")
    public RestResponse build(Long id) throws Exception {
        return projectService.build(id);
    }

    @RequestMapping("list")
    public RestResponse list(Project project, RestRequest restRequest) {
        IPage<Project> page = projectService.page(project, restRequest);
        return RestResponse.create().data(page);
    }

    @RequestMapping("select")
    public RestResponse select() {
        return RestResponse.create().data(projectService.list());
    }

    @RequestMapping("delete")
    public RestResponse delete(String id) {
        boolean result = projectService.delete(id);
        return RestResponse.create().message(result ? "删除成功" : "删除失败");
    }

    @RequestMapping("filelist")
    public RestResponse filelist(String id) {
        List<Map<String,Object>> result = projectService.filelist(id);
        return RestResponse.create().data(result);
    }

}
