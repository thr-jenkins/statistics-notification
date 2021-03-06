package org.jenkins.plugins.statistics.util;

import hudson.EnvVars;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import org.jenkins.plugins.statistics.StatisticsConfiguration;

import java.util.List;
import java.util.ResourceBundle;


public class PropertyLoader {
  public static final String DEFAULT_PROPERTY_FILE_NAME = "statistics";
  private static PropertyLoader s_instance = null;
  private final ResourceBundle rb;

  private PropertyLoader() {
    rb = ResourceBundle.getBundle(DEFAULT_PROPERTY_FILE_NAME);
  }

  public synchronized static final PropertyLoader getInstance() {
    if (s_instance == null) {
      s_instance = new PropertyLoader();
    }
    return s_instance;
  }

  /**
   * This should only be called when the process is being setup.  It
   * is not intended for general use.
   */
  public synchronized static final void setInstance(
      final PropertyLoader propertyLoader) {
    s_instance = propertyLoader;
  }

  protected String getResourceBundleProperty(String keyProperty) {
    return rb.getString(keyProperty);
  }

  /**
   * Utility method to get the properties value
   * First it will try to get the property value from the vars supplied
   * then from the property file
   *
   * @param inKey for which value will be returned
   *              the properties are looked thru the property file
   * @return the value of the key
   * @throws Exception
   */
  public String getProperty(
      final String inKey) {
    if (inKey == null) {
      return null;
    }
    final String key = inKey.trim();
    if (key.length() <= 0) {
      return null;
    }
    final EnvVars vars = new EnvVars();
    DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalProps =
        Jenkins.getInstance().getGlobalNodeProperties();
    List<EnvironmentVariablesNodeProperty> properties =
        globalProps.getAll(EnvironmentVariablesNodeProperty.class);
    for (EnvironmentVariablesNodeProperty environmentVariablesNodeProperty : properties) {
      vars.putAll(environmentVariablesNodeProperty.getEnvVars());
    }
    final String value = vars.get(key);
    if (value == null || value.isEmpty()) {
      return getResourceBundleProperty(key);
    }
    return value;
  }

  /**
   * Utility method to get the properties value
   * First it will try to get the property value from the vars supplied
   * then from the property file
   *
   * @param key for which value will be returned
   * @return the value of the key
   * @throws Exception
   */
  public static String getEnvironmentProperty(
      final String key) {
    return getInstance().getProperty(key);
  }

  public static String getStatsEndPoint() {
//    String endPoint = StatsPlugin.DescriptorImpl.getNotificationUrl();
    String endPoint = StatisticsConfiguration.get().getNotificationUrl();
    if (endPoint !=null && !endPoint.isEmpty()) {
      return endPoint;
    }
    endPoint = getEnvironmentProperty("statistics.endpoint.url");
    return endPoint == null ? "" : endPoint;
  }
}