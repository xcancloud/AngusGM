## Plugin Path

- Set through WM Options parameter

```properties
-Dplugin.dir=/Volumes/workspace/workspace_angus/seek/AngusGM/service/extension/dist
```

- Default plugin path reading

In the application's Home directory: ./plugins

- Jenkins build and plugin path setting

```bash
extension_dist="./extension/dist"
...
mkdir $target/plugins && cp -r $extension_dist/*  $target/plugins/
```

## Private Deployment

- Notes:

    * For private versions, users need to configure SMS channel access information themselves; when
      released, XiaoCan third-party SMS channel access information should be deleted.
    * Template data with channel_id of -1 is initialization data. Each time the SMS plugin is
      installed, template data will be initialized for each SMS channel. (Subsequent template
      additions and modifications need to be manually synchronized across various plugin channels)
