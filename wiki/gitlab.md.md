
`gitlab`导入通过 `gitlab export` 的时候当版本不一致时, 提示错误
`"Import version mismatch: Required 0.2.4 but was 0.2.0"`时, 可通过以下方式修复, 
注意, 此方式本人只是试了`gitlab-ce-10.0.0`的包导入到`gitlab-ce-14.x`版本验证通过, 不保证所有版本可行:

```
mkdir project_export
tar xfv old_export_file.tar.gz -C project_export
cd project_export
echo '0.2.4' > VERSION
tar czf experimental_new_project_export.tar.gz *

```