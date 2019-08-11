package com.example.apollodynamicdatasource.dynamicDataSource.properties;

import lombok.Data;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.util.List;

@Data
public class DynamicDataSourceProperties {
	private ClassLoader classLoader;
	private String name;
	private boolean generateUniqueName;
	private Class<? extends DataSource> type;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private String jndiName;
	private DataSourceInitializationMode initializationMode;
	private String platform;
	private List<String> schema;
	private String schemaUsername;
	private String schemaPassword;
	private List<String> data;
	private String dataUsername;
	private String dataPassword;
	private boolean continueOnError;
	private String separator;
	private Charset sqlScriptEncoding;
	private EmbeddedDatabaseConnection embeddedDatabaseConnection;
	private org.springframework.boot.autoconfigure.jdbc.DataSourceProperties.Xa xa;
	private String uniqueName;
	private Integer maximumPoolSize;
}
