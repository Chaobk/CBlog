package com.chaobk.util.upload;

import com.chaobk.constant.UploadConstants;
import com.chaobk.exception.BadRequestException;
import com.chaobk.util.upload.channel.ChannelFactory;
import com.chaobk.util.upload.channel.FileUploadChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @Description: 上传工具类
 * @Author: Naccl
 * @Date: 2021-11-11
 */
@Component
@Slf4j
@DependsOn("springContextUtils")
public class UploadUtils {
	private static RestTemplate restTemplate;

	private static FileUploadChannel uploadChannel;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		UploadUtils.restTemplate = restTemplate;
	}

	@Value("${upload.channel}")
	public void setNotifyChannel(String channelName) {
		UploadUtils.uploadChannel = ChannelFactory.getChannel(channelName);
	}

	@AllArgsConstructor
	@Getter
	public static class ImageResource {
		byte[] data;
		//图片拓展名 jpg png
		String type;
	}

	/**
	 * 通过指定方式存储图片
	 *
	 * @param image 需要保存的图片
	 * @throws Exception
	 */
	public static String upload(ImageResource image) throws Exception {
		return uploadChannel.upload(image);
	}

	/**
	 * 通过文件路径上传文件到oss
	 * @param filepath 要上传的文件的存放位置
	 * @return 文件的OSS链接
	 * @throws IOException
	 */
	public static String upload(String filepath) throws IOException {
		return uploadChannel.upload(filepath);
	}

	/**
	 * 从网络获取图片数据
	 *
	 * @param url 图片URL
	 * @return
	 */
	public static ImageResource getImageByRequest(String url) {
		ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
		if (UploadConstants.IMAGE.equals(responseEntity.getHeaders().getContentType().getType())) {
			return new ImageResource(responseEntity.getBody(), responseEntity.getHeaders().getContentType().getSubtype());
		}
		throw new BadRequestException("response contentType unlike image");
	}

}
