# git

### git base command <a href="http://blog.yubangweb.com/kan-wan-jiu-hui-yong-de-gitcao-zuo-tu-jie-fen-xi/" target="_blank">see here</a>
> base
```
git clone x
git add y
git commit -m 'z'
git push
```

```
拉取远程分支到本地
git checkout -b 本地分支名 origin/远程分支名
git checkout -b local.branch.name origin/remote.branch.name

http://blog.it985.com/753.html

1 查看远程分支
$ git branch -a

 2 查看本地分支

 $ git branch

3 创建分支
  git branch test



修改地址:
git remote set-url origin http://xxxx.git

git低版本使用用户名密码方式
git clone http://用户名:密码@xxx.com/yyy.git


git 常用命令
基本命令

代码检出：git clone 地址

文件修改添加到暂存区：git add readme.txt

文件提交：git commit -m “备注”

查看工作区当前状态：git status

查看差异：git diff 文件名

查看历史：git log –pretty=oneline

查看历史提交commit id：git log –pretty=oneline –abbrev-commit

恢复当前版本：git reset –hard HEAD

恢复上一个版本：git reset –hard HEAD^
（上上一个版本就是HEAD^^，当然往上100个版本写100个^比较容易数不过来，所以写成HEAD~100）

恢复指定版本：git reset –hard 3628164

查看命令历史：git reflog

查看工作区和版本库区别：git diff HEAD – readme.txt

撤销修改：git checkout – readme.txt

撤销缓存区文件：git reset HEAD readme.txt

删除文件：git rm test.txt

分支命令

查看分支：git branch

创建分支：git branch 

切换分支：git checkout 

创建+切换分支：git checkout -b 

推送远程分支：git push origin 

创建远程分支：git checkout -b dev origin/dev

git checkout -b paytest_20151202_online origin/paytest_20151202_online

合并某分支到当前分支：git merge 

合并分支禁用Fast forward：git merge –no-ff -m “备注” dev

删除分支：git branch -d 

删除远程分支：git push origin –delete 
git branch -r -d origin/branch.name
git push origin :branch.name

强制删除分支：git branch -D 

查看分支历史：git log –graph –pretty=oneline –abbrev-commit

查看分支提交时间: git reflog show --date=iso branch.name

分支合并图：git log –graph

储藏工作区：git stash

查看储藏的工作区：git stash list

恢复并删除储藏工作区：git stash pop

恢复指定工作区：git stash apply stash@{0}

恢复储藏工作区：git stash apply

删除储藏工作区：git stash drop

查询远程库详细信息：git remote -v

推送分支：git push origin dev

抓取最新文件：git pull 分支名

取远程分支：git pull origin 分支名

指定分支与远程分支链接：git branch –set-upstream dev origin/dev

标签命令

创建标签：git tag v1.0

删除本地标签：git tag -d v0.1

删除远程标签：git push origin :refs/tags0.9

删除远程标签：git push origin –delete tag 

查看标签：git tag

指定commit id创建标签：git tag v0.9 6224937

指定标签信息：git tag -a -m “blablabla…”

可以用PGP签名标签：git tag -s -m “blablabla…”

查看标签信息：git show 标签名

推送标签到远程：git push origin v1.0

推送所有标签到远程：git push origin –tags

其他

配置别名：git config –global alias.st status

在Git工作区的根目录下创建一个特殊的.gitignore文件，然后把要忽略的文件名填进去，Git就会自动忽略这些文件。

撤銷git add . => git rm -r –cached .
```