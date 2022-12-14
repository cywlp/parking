package com.cgsx.parking_system.controller;

import com.cgsx.parking_system.constants.Constants;
import com.cgsx.parking_system.entity.Space;
import com.cgsx.parking_system.entity.SpaceArea;
import com.cgsx.parking_system.service.SpaceAreaService;
import com.cgsx.parking_system.service.SpaceService;
import com.cgsx.parking_system.util.DefinitionException;
import com.cgsx.parking_system.util.ErrorEnum;
import com.cgsx.parking_system.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/space")
@Slf4j
public class SpaceController {

    @Autowired
    private SpaceService spaceService;
    @Autowired
    private SpaceAreaService spaceAreaService;

    @PostMapping("/updateSpace")
    public Result updateSpace(@RequestBody Space space){
        spaceService.updateSpace(space);
        log.info("请求接口updateSpace,body:"+space.toString());
        return new Result().success("更新成功", space);
    }

    @PostMapping("/editIsOnline")
    public Result editIsOnline(@RequestBody Space space){
        log.info("请求接口editIsOnline， body:" + space);
        if(space.getSpaceStatus() == 1)
            return Result.otherError(ErrorEnum.SPACE_CAR_EXIST);
        if(space.getSpaceStatus() != 2)
            space.setSpaceStatus(2);
        else
            space.setSpaceStatus(0);
        spaceService.updateSpace(space);
        return new Result().success("成功", space);
    }

    @PostMapping("/addSpace")
    public Result addSpace(@RequestBody Space space){
        SpaceArea spaceArea = spaceAreaService.getSpaceAreaById(space.getSpaceArea().getSpaceAreaId());
        space.setSpaceArea(spaceArea);
        log.info("请求接口addSpace,body:" + space);
        if(spaceService.existsSpace(space.getSpaceArea(), space.getSpaceNum())){
            throw new DefinitionException(400, "该区域车位编号已存在");
        }
        spaceService.updateSpace(space);
        return new Result().success("创建成功", space);
    }

    @RequestMapping("/listSpace")
    public Result querySpace(
            @RequestParam(name = "keyword", defaultValue = "")String keyword,
            @RequestParam(name = "spaceStatus", defaultValue = "-1")Integer spaceStatus,
            @RequestParam(name = "page", defaultValue = "0")Integer pageNumber,
            @RequestParam(name = "limit", defaultValue = "10")Integer pageSize){

        log.info("请求接口listSpace,参数:keyword:"+keyword+"spaceStatus"+spaceStatus+"pageNumber"+pageNumber+"pageSize"+pageSize);
        return new Result().success("查询成功", spaceService.getSpace(keyword, spaceStatus, pageNumber, pageSize));
    }
}
