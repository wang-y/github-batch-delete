批量删除Github中的仓库


根据电脑的系统及chrome版本，在 http://chromedriver.storage.googleapis.com/index.html 中寻找对应的Driver

下载后将解压出来的chromedriver文件放入resources/driver目录下

对应系统分别修改名称：Windows: chromedriver.exe, MacOS: chromedriver_mac, Linux: chromedriver_linux

修改application.yml的配置
```yaml

github:
  username: username # 用户名
  password: password # 密码
  repositorys: repo1,repo2,repo3 # 仓库名称，使用','分割
  
# Windows系统请维护chrome安装路径
chrome:
  win:
    bin: C:\Program Files (x86)\Google\Chrome\Application\chrome.exe

```