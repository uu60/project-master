<template>
  <div>
    <el-container>
      <el-aside width="70%"></el-aside>
      <el-main>
        <div id="login">
          <!--          <h1 v-cloak>{{ this.$store.state.projectName }}</h1>-->
          <h1>Login</h1>
          <el-form
              ref="form"
              label-width="80px"
              :rules="rules"
              :model="form"
              @keyup.enter.native="doLogin"
          >
            <el-form-item label="Username" prop="username">
              <el-input v-model="form.username"></el-input>
            </el-form-item>
            <el-form-item label="Password" prop="password">
              <el-input v-model="form.password" show-password></el-input>
            </el-form-item>
            <el-form-item style="text-align: center">
              <el-button type="primary" @click="doLogin" v-loading="loginning"
              >login
              </el-button
              >
              <el-button
                  type="text"
                  style="float: right"
                  @click="dialogFormVisible = true"
              >Account registraion
              </el-button
              >
            </el-form-item>
          </el-form>
        </div>
      </el-main>
    </el-container>
    <el-dialog title="Account Registraion" :visible.sync="dialogFormVisible" width="500px">
      <el-form
          ref="form1"
          :model="form1"
          style="margin-left: 20%; margin-right: 20%"
          :rules="rules"
      >
        <el-form-item label="Username" prop="username">
          <el-input v-model="form1.username"></el-input>
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="form1.password" show-password></el-input>
        </el-form-item>
        <el-form-item style="text-align: center">
          <el-button type="primary" @click="doRegister" v-loading="registering"
          >Registration
          </el-button
          >
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import md5 from "js-md5";
import axios from "axios";

export default {
  data: function () {
    return {
      form: {
        username: "",
        password: "",
      },
      form1: {},
      dialogFormVisible: false,
      loginning: false,
      registering: false,
      rules: {
        username: [
          {required: true, message: "Username can not be empty.", trigger: "blur"},
        ],
        password: [
          {required: true, message: "Password can not be empty", trigger: "blur"},
          {min: 6, message: "Password has at least 6 characters.", trigger: "blur"},
        ],
      },
    };
  },
  beforeCreate() {
    // console.log(this.$store.state.backendAddress)
    if (this.$store.state.token) {
      this.$router.push("/SearchPage");
    }
  },

  methods: {
    doLogin() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this.loginning = true;
          if (this.$store.state.token) {
            // console.log("forbidden relogin.");
            this.$router.push({
              name: "SearchPage",
            });
            return;
          }
          axios.post('http://localhost:8080/denglu/auth/api/v1/login', JSON.stringify({
            username: this.form.username,
            password: this.form.password,
          }, {
            headers: {
              'Content-Type': 'application/json'
            }
          }))
              .then((res) => {
                // console.log("登录成功")
                console.log(res)
                // code == 200说明登录验证成功
                if (res.data == '') {
                  // console.log("received login token: " + res.data.token);
                  // console.log("this.username = " + this.username + " will be stored");
                  // console.log(res.headers.authorization)
                  // console.log(this.form.username)
                  this.$store.commit("setToken", res.headers.authorization);
                  this.$store.commit("setUsername", this.form.username);
                  this.$router.push("/SearchPage");
                }
                 else if (res.data.code == 3) {
                  this.$message.error("Wrong username or password.");
                } else {
                  this.$message.error("Something wrong.");
                }
                this.loginning = false;
              })
              .catch((err) => {
                this.$message.error(this.$store.state.serverErrMsg);
                console.log(err.msg);
                this.loginning = false;
              });
        }
      });
    },
    doRegister() {
      this.registering = true;
      this.$refs["form1"].validate((valid) => {
        if (valid) {
          axios.post('http://localhost:8080/registe/auth/api/v1/register', JSON.stringify({
            username: this.form1.username,
            password: this.form1.password,
          }), {
            headers: {
              'Content-Type': 'application/json'
            }
          })
              .then((res) => {
                console.log(res.data)
                if (res.data.code == 0) {
                  this.$message({
                    type: "success",
                    message: "Registration Success",
                  });
                  this.dialogFormVisible = false;
                } else if (res.data.code == 1) {
                  this.$message.error("Duplicate name");
                } else if (res.data.code == 2) {
                  this.$message.error("Password is too short.");
                } else {
                  this.$message.error(this.$store.state.serverErrMsg);
                }
                this.registering = false;
              })
              .catch((err) => {
                console.log(err);
                this.$message.error(this.$store.state.serverErrMsg);
                this.registering = false;
              });
        } else {
          this.registering = false;
        }
      });
    },
  },
};
</script>

<style>
body {
  margin: 0;
}
</style>

<style scoped>
.el-container {
  background: url("~@/assets/aside.jpeg");
  background-size: 300px;
  height: 100vh;
  min-width: 1000px;
}

#login {
  width: 100%;
  max-width: 300px;
}

.el-main {
  padding: 20px;
  height: 100%;
  background: rgba(230, 235, 228, 0.9);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

h1 {
  text-align: center;
}
</style>
