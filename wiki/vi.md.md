   常用命令:

```shell
# i, 当前光标位置输入, insert
i

# a, 当前光标后面输入
a

# :g/aaa/s//bbb/g, 全局把aaa字符串替换成bbb
:g/aaa/s//bbb/g

# :%s/^/public some/, 在每行行首加入public some字符串
:%s/^/public some/

# o, 下一行插入
o

# p, 下一行粘贴
p

# yy, 复制当前行
yy

# 3yy, 复制当前行和后2行, 共3行
3yy

# dd, 删除当前行
dd

# 3dd, 删除当前后及后2行
3dd

# G, 文末
G

# gg, 文首
gg

# :set nu, 显示行号
:set nu

# :30, 到第30行
:30

# shift+3, 搜索当前光标所在字符串
shift+3

# n, 下一个匹配
n

# N, 上一个匹配
N

# /aaa, 搜索aaa字符串
/aaa

# ?aaa, 向前搜索aaa字符串
?aaa

# :set rnu, 显示对应当前光标位置的相对行号
:set rnu

# %, 跳到到下一个成对匹配的地方,  如{},[],()等
%

# di{, 删除光标所在位置的{}内的内容, 可以是di", di[, di(, 等
di"


``` 


