# Logstash Input Kotlin Plugin

This Kotlin plugin is based on the Java Input Plugin available here:
 
https://www.elastic.co/guide/en/logstash/current/java-input-plugin.html
 
### To build the plugin

```
./gradlew build gem
```

### To install

```
bin/logstash-plugin install --no-verify --local /path/to/logstash-filter-kotlin_filter_plugin-0.0.1.gem
```

### To test

Default Conf:
```
bin/logstash -f /path/to/kotlin_input_plugin_default.conf
```
Custom Conf: 
```
bin/logstash -f /path/to/kotlin_input_plugin_custom.conf
```