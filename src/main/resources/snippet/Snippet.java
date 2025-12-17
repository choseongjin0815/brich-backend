package snippet;

public class Snippet {
	mybatis:
	  config-location: classpath:mybatis/config.xml
	  mapper-locations: classpath:com/ktdsuniversity/edu/**/mapper/*Mapper.xml
	  
	spring:
	  security:
	     oauth2:
	       client:
	         registration:
	           naver:
	             client-id: If2nlnpD1rYbJT9fIhul
	             client-secret: oo_cjsKkYL
	             redirect-uri: http://localhost:8080/login/oauth2/code/naver
	             authorization-grant-type: authorization_code
	             scope: name, email, nickname, gender
	             client-name: Naver
	         provider:
	           naver:
	             authorization-uri: https://nid.naver.com/oauth2.0/authorize
	             token-uri: https://nid.naver.com/oauth2.0/token
	             user-info-uri: https://openapi.naver.com/v1/nid/me
	             user-name-attribute: response
	  servlet:
	    multipart:
	      max-file-size: 100MB #1024(1kb) * 1024(1mb) * 100(100mb)
	      max-request-size: 120MB
	  datasource:
	    url: jdbc:oracle:thin:@3.38.95.135:1521:XE
	    driver-class-name: oracle.jdbc.driver.OracleDriver
	    username: brich 
	    password: 1234
	  data:
	    mongodb:
	      uri: mongodb+srv://brich1020:brichproject1!@brichcluster.qdemey6.mongodb.net/brich?retryWrites=true&w=majority
	  application:
	    name: hello-spring
	  mail:
	    host: smtp.gmail.com
	    port: 587
	    username: brichproject1020@gmail.com
	    password: stdbvfjgcyoydbbi
	    properties:
	      mail:
	        smtp:
	          auth: true
	          starttls.enable: true
	          starttls.required: true
	          
	  devtools:
	    livereload:
	      enabled: true
	  # 템플릿 엔진 충돌/오인 방지 (의존성도 없지만 혹시 모를 자동 설정 차단)
	  thymeleaf:
	    enabled: false
	  freemarker:
	    enabled: false
	logging:
	    level:
	      root: info
	          
	 
	  # hello-spring application의 별도 설정(Customize)
	app:
	  jwt:
	    issuer: ktds-university-brich
	    secret-key: 239dfkle2ljkdlfj290dxk4kdjv0x3k2
	   # 그대로 사용 중인 커스텀 키 (필요 시 app.api.key 등으로 네임스페이스 변경 가능)
	  api:
	    key: test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6
	  multipart:
	  #업로드 가능한 파일들의 목록
	  #업로드 가능한 목록이 더 작기에 가능한 애들 적고 업로드시마다 검사한다.
	    whitelist: 
	      .txt,
	      .yml,
	      .html,
	      .css,
	      text/html,
	      image/png,
	      image/gif,
	      image/jpeg,
	      image/tiff,
	      application/pdf,
	      application/x-tika-ooxml,
	      application/x-tika-msoffice,
	      application/zip,
	      text/plain
	#사용자가 업로드한 파일이 저장될 위치 
	# C:/uploadFiles
	#    base-dir: /Users/choseong-jin/Documents/uploadFiles
	    base-dir:
	      windows: C:/uploadFiles
	      macos: /Documents/uploadFiles
	    obfuscation:
	#파일명을 난독화 할 것인지에 대한 여부
	      enable: true
	#난독화를 할 때 확장자를 가릴것인지 아닌지에 대한 설정
	      hide-ext: 
	        enable: true
	naver-api:
	  clientId: "tFkm1h795OKRNPMxt5uK"
	  clientSecret: "Y3JVU2efH5"
	            
	server:
	  servlet:
	    encoding:
	      charset: UTF-8
	      enabled: true
	      force: true
	
	  
}

