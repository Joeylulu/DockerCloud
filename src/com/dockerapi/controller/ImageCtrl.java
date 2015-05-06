package com.dockerapi.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dockerapi.client.DockerImagesImpl;
import com.dockerapi.dao.DBConn;
import com.dockerapi.model.ContainerPost;
import com.google.gson.Gson;

@Component
@Controller
public class ImageCtrl {

	@Resource
	private DBConn dbConn;
	private DockerImagesImpl imagesImpl;

	@Resource
	public void setImagesImpl(DockerImagesImpl imagesImpl) {
		this.imagesImpl = imagesImpl;
	}

	@RequestMapping("/image/list")
	public ModelAndView listImages() {

		// get ip:port from .properties
		String dockerUrl = "/json";
		imagesImpl.setDockerUrlResource(dockerUrl, 0);
		imagesImpl.listImages();

		ModelAndView mv = new ModelAndView("images");
		mv.addObject("count", imagesImpl.getiList().size());
		mv.addObject("imgs", imagesImpl.getiList());
		return mv;
	}

	@RequestMapping("/image/run")
	public ModelAndView runImages(String i) {
		System.out.println(i);
		String name = imagesImpl.getiList().get(Integer.parseInt(i)).getName();
		System.out.println(name);

		String cmdJson = dbConn.imageInfo(name, "cmdJson");
		String dockerUrl = "/create?name=" + name;
		imagesImpl.setDockerUrlResource(dockerUrl, 1);
		imagesImpl.runContainers(cmdJson);

		// phpmyadmin for mysql
		if (name.equals("mysql")) {
			dockerUrl = "/create?name=phpmyadmin";
			cmdJson = dbConn.imageInfo("phpmyadmin", "cmdJson");
			imagesImpl.setDockerUrlResource(dockerUrl, 1);
			imagesImpl.runContainers(cmdJson);
		}
		return listImages();
	}

	@RequestMapping("/image/remove")
	public ModelAndView removeImages(String i) {

		System.out.println(i);
		String name = imagesImpl.getiList().get(Integer.parseInt(i)).getName();
//		String repoTag = imagesImpl.getiList().get(Integer.parseInt(i)).getRepoTags()[0].split(":")[1];
//		System.out.println(name + ":" + repoTag);
		// get ip:port from .properties
		String id = imagesImpl.getiList().get(Integer.parseInt(i)).getId();
		String dockerUrl = "/" + id;
		imagesImpl.setDockerUrlResource(dockerUrl, 0);
		imagesImpl.removeImages();

		return listImages();
	}

}
