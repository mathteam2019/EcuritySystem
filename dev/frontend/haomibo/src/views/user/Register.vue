<style>
  div.invalid-feedback {
    display: block;
  }
</style>
<template>
  <b-row class="h-100">
    <b-colxx xxs="12" md=10 class="mx-auto my-auto">
      <b-card class="auth-card" no-body>
        <div class="position-relative image-side ">
          <p class=" text-white h2">{{ $t('pages.project-name') }}</p>
          <p class="white mb-0"> {{ $t('pages.register-desc') }} .
          </p>
        </div>
        <div class="form-side">
          <router-link tag="a" to="/"><span class="logo-single"/></router-link>
          <h6 class="mb-4">{{ $t('user.register')}}</h6>
          <b-form @submit.prevent="formSubmit">
            <div>
              <label class="form-group has-float-label mb-4">
                <input type="text" class="form-control" v-model="registerForm.name">
                <span>{{ $t('user.name') }}</span>
                <div v-if="submitted&&!$v.registerForm.name.required" class="invalid-feedback">Name is required</div>
                <div v-else-if="!$v.registerForm.name.alphaNum" class="invalid-feedback">Name should be numerical or characters</div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="email" class="form-control" v-model="registerForm.email">
                <span>{{ $t('user.email') }}</span>
                <div v-if="submitted&&!$v.registerForm.email.required" class="invalid-feedback">Email is required</div>
                <div v-else-if="!$v.registerForm.email.email" class="invalid-feedback">Email should be valid format</div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="password" class="form-control" v-model="registerForm.password">
                <span>{{ $t('user.password') }}</span>
                <div v-if="submitted&&!$v.registerForm.password.required" class="invalid-feedback">Password is required</div>
                <div v-else-if="!$v.registerForm.password.minLength" class="invalid-feedback">Password must be at least 6 characters</div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="password" class="form-control" v-model="registerForm.confPassword">
                <span>{{ $t('user.conf-password') }}</span>
                <div v-if="submitted&&!$v.registerForm.confPassword.required" class="invalid-feedback">Password Confirmation is required</div>
                <div v-else-if="!$v.registerForm.confPassword.sameAsPassword" class="invalid-feedback">Confirmation  should be match as Password</div>
              </label>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <router-link tag="a" to="/user/login">{{ $t('menu.login') }}</router-link>
              <b-button type="submit"  variant="success" size="lg" :disabled="$v.registerForm.$invalid"
                        class="btn-shadow">{{
                $t('user.register-button')}}
              </b-button>
            </div>
          </b-form>
        </div>
      </b-card>
    </b-colxx>
  </b-row>
</template>
<script>
  import 'vue-select/dist/vue-select.css'
  import {validationMixin} from 'vuelidate'
  import {getDirection} from '../../utils'
  import {mapGetters, mapActions} from 'vuex'
  const {required, email, minLength, sameAs, alphaNum} = require('vuelidate/lib/validators');

  export default {
    data() {
      return {
        registerForm: {
          name: '',
          email: '',
          password: '',
          confPassword: '',
        },
        submitted:false
      }
    },
    computed: {
      ...mapGetters(['currentUser', 'processing', 'loginError'])
    },
    mixins: [validationMixin],
    validations: {
      registerForm: {
        name: {
          required, alphaNum
        },
        password: {
          required, minLength: minLength(6)
        },
        confPassword: {
          required, sameAsPassword: sameAs('password')
        },
        email: {
          required,
          email
        }
      }
    },
    methods: {
      ...mapActions(['register']),
      formSubmit() {
        this.submitted = true;
        this.$v.$touch();
        if (this.$v.$invalid) {
          return;
        }
        this.register(this.registerForm)
      }
    },
    watch: {
      currentUser(val) {
        if (val) {
          this.$notify('success', this.$t('user.success'), this.$t(`user.register-success`), {
            duration: 3000,
            permanent: false
          });
          setTimeout(() => {
            this.$router.push('/user/login')
          }, 100)
        }
      },
      loginError(val) {
        if (val != null) {
          this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.${val}`), {
            duration: 3000,
            permanent: false
          })
        }
      }
    },

  }
</script>
