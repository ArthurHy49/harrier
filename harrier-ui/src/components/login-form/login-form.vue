<template>
  <Form ref="loginForm" :model="form" :rules="rules" @keydown.enter.native="handleSubmit">
    <FormItem prop="username">
      <Input type="text" size="large"  v-model="form.userName" icon="ios-person" placeholder="请输入用户名" ></Input>
    </FormItem>
    <FormItem prop="password">
      <Input type="password" size="large" v-model="form.password" icon="ios-lock" placeholder="请输入密码" ></Input>
    </FormItem>
    <FormItem prop="code" v-if="captchaSwitch">
            <div style="height: 32px;display: inline;">

      <Input v-model="form.code" placeholder="验证码" style="width:170px;display:inline-table" icon="ios-bug">
      </Input>
        <img :src="codeUrl" @click="getCode" class="login-code-image" style="height:32px ;margin-bottom:-12px;margin-left:20px"/>
    </div>
    </FormItem>
    <FormItem>
      <Button @click="handleSubmit" type="primary" long>登录</Button>
    </FormItem>
  </Form>
</template>
<script>
export default {
  name: 'LoginForm',
  props: {
    userNameRules: {
      type: Array,
      default: () => {
        return [
          { required: true, message: '账号不能为空', trigger: 'blur' }
        ]
      }
    },
    passwordRules: {
      type: Array,
      default: () => {
        return [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ]
      }
    },
    captchaSwitch: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      codeUrl: '',
      form: {
        userName: '',
        password: ''
      }
    }
  },
  computed: {
    rules () {
      return {
        userName: this.userNameRules,
        password: this.passwordRules
      }
    }
  },
  watch: {
    captchaSwitch: {
      handler (newValue, oldValue) {
        if (newValue) {
          this.getCode()
        }
      },
      deep: true
    }

  },
  methods: {
    getCode () {
      let params = {}
      params.uuid = this.uuid
      let loadConfig = {
        method: 'GET',
        url: '/user/captchImage',
        params: params
      }
      this.$ajax(loadConfig)
        .then(resp => {
          this.codeUrl = resp.data.data.img
          this.uuid = resp.data.data.uuid
        })
    },
    handleSubmit () {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.$emit('on-success-valid', {
            userName: this.form.userName,
            password: this.form.password,
            code: this.form.code,
            uuid: this.uuid
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.login-code-image {
  height:32px ;
  margin-bottom:-12px;
  margin-left:20px
}
</style>
