   

```bash

# 可视化记录
gitk --all

# 导出裸库
git clone --bare xx.git

# 推送裸库到新的地址
git push --mirror xxx.git

# 裸库bare转换git，不需要远程git地址
mkdir xxx
cd xxx && mkdir .git
复制裸库下面的所有文件到.git下
git config --local --bool core.bare false
git reset --hard

```