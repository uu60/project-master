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
### 2 Install RabbitMQ

```shell
yum install epel-release
# install dependency erlang
wget http://packages.erlang-solutions.com/erlang/rpm/centos/7/x86_64/esl-erlang_25.0.3-1~centos~7_amd64.rpm
yum install esl-erlang_25.0.3-1~centos~7_amd64.rpm

# install rabbitmq
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.12.0/rabbitmq-server-3.12.0-1.el8.noarch.rpm
rpm -Uvh rabbitmq-server-3.12.0-1.el8.noarch.rpm 

# start rabbitmq
systemctl start rabbitmq-server
systemctl enable rabbitmq-server

# install web management plugin
rabbitmq-plugins enable rabbitmq_management
```

### 3 Install Nginx (only on server1)

Reference: https://blog.csdn.net/qq_33381971/article/details/123328191

```shell
yum -y install gcc gcc-c++ make libtool zlib zlib-devel openssl openssl-devel pcre pcre-devel
wget https://nginx.org/download/nginx-1.25.0.tar.gz
tar -zxvf nginx-1.25.0.tar.gz
cd nginx-1.25.0
./configure --prefix=/usr/local/nginx
make
make install
echo 'export PATH=$PATH:/usr/local/nginx/sbin' >> /etc/profile
```

