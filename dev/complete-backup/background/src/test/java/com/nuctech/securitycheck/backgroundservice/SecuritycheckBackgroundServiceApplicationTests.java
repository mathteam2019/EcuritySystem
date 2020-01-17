package com.nuctech.securitycheck.backgroundservice;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerDevLog;
import com.nuctech.securitycheck.backgroundservice.common.models.SysDeviceVersionModel;
import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import com.nuctech.securitycheck.backgroundservice.message.MessageSender;
import com.nuctech.securitycheck.backgroundservice.repositories.SerDevLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles
public class SecuritycheckBackgroundServiceApplicationTests {
	@Autowired
	ApplicationContext context;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private SerDevLogRepository serDevLogRepository;

	@Test
	public void contextLoads() {
		SerDevLog serDevLog = new SerDevLog();
		serDevLog.setGuid("guid0001");
		Example<SerDevLog> ex = Example.of(serDevLog);
		serDevLog = serDevLogRepository.findOne(ex);

		String topicExchange = BackgroundServiceUtil.getConfig("topic.inter.dev.sys");
		String routingKey = BackgroundServiceUtil.getConfig("routingKey.sys.updateversion");

		SysDeviceVersionModel sysDeviceVersionModel = new SysDeviceVersionModel();
		sysDeviceVersionModel.setGuid("tempGuid");
		sysDeviceVersionModel.setAlgorithmVersion("1.0");
		sysDeviceVersionModel.setSoftwareVersion("1.0");

		Object obj = new Object();
		obj = (Object) sysDeviceVersionModel;

		//messageSender.sendSysDeviceVersionMessage(topicExchange, routingKey, obj);
	}
}
