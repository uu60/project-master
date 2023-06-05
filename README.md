# project-master

2023/06/03

### 1 Install MySQL, Redis

#### 1.1 Install MySQL

Reference: https://blog.csdn.net/u010881625/article/details/126401832

1. First, execute the following scripts to download and install:

```shell
wget https://repo.mysql.com//mysql80-community-release-el7-3.noarch.rpm
yum -y install mysql80-community-release-el7-3.noarch.rpm
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
yum -y install mysql-community-server
systemctl start mysqld.service
systemctl enable mysqld.service
```

2. Use the following script to see temporary password:

`cat /var/log/mysqld.log | grep password`

3. Use the temp password to login and change the password of root with a complex one temporarily:

`ALTER USER 'root'@'localhost' IDENTIFIED BY 'your password';`

4. Then modify the password level requirements:

```mysql
set global validate_password.policy=0; # low level
set global validate_password.length=4; # length requirement
```

5. Modify the password with a simpler one:

`ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';`

#### 1.2 Install Redis

Reference: https://blog.csdn.net/Crazy_young_man/article/details/105729646

1. Download and install by yum:

```shell
yum install epel-release
yum install redis
```

2. Modify the configuration (make a backup first)

```shell
cd /etc
cp redis.conf redis.conf.bak
vim redis.conf
```

3. Enter `:set number` and press enter. 
   Then modify the line 61 with: `#bind 127.0.0.1` (add comment)
   Modify line 80 with: `protected-mode no`
   Modify line 128 with: `daemonize yes`
   Enter `:wq` and press enter. 

4. Start Redis at machine's starting up.

```shell
systemctl start redis.service
systemctl enable redis.service
```

<hr>

2023/06/05

### 1 Server List

1. 8.137.96.5
2. 8.137.98.1
3. 47.109.79.121
4. 47.109.56.80
