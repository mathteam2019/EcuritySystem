<template>
  <b-row class="h-100">
    <b-colxx xxs="12" md=10 class="mx-auto my-auto">
      <b-card class="auth-card" no-body>
        <div class="position-relative image-side ">
          <p class=" text-white h2">{{ $t('pages.project-name') }}</p>
          <p class="white mb-0">{{ $t('pages.enter-login-information') }}
          </p>
        </div>
        <div class="form-side">
          <router-link tag="a" to="/"><span class="logo-single"/></router-link>
          <h6 class="mb-4">{{ $t('user.login-title')}}</h6>
          <b-form @submit.prevent="formSubmit">
            <label class="form-group has-float-label mb-4">
              <input type="email" class="form-control" v-model="email">
              <span>{{ $t('user.email') }}</span>
            </label>
            <label class="form-group has-float-label mb-4">
              <input type="password" class="form-control" v-model="password">
              <span>{{ $t('user.password') }}</span>
            </label>
            <div class="d-flex justify-content-between align-items-center">
              <router-link tag="a" to="/user/register">{{ $t('user.register-button')}}</router-link>
              <b-button type="submit" variant="primary" size="lg" class="btn-shadow" :disabled="processing">{{
                $t('user.login-button')}}
              </b-button>
            </div>
          </b-form>
        </div>
      </b-card>
    </b-colxx>
  </b-row>
</template>
<script>
  import {mapGetters, mapActions} from 'vuex'

  export default {
    data() {
      return {
        email: '',
        password: ''
      }
    },
    computed: {
      ...mapGetters(['currentUser', 'processing', 'loginError'])
    },
    methods: {
      ...mapActions(['login']),
      formSubmit() {

        if (this.email.length === 0) {
          this.$notify('warning', this.$t('user.warning'), this.$t(`user.enter-valid-email`), {
            duration: 3000,
            permanent: false
          });
          return;
        }
        if (this.password.length < 6) {
          this.$notify('warning', this.$t('user.warning'), this.$t(`user.enter-valid-password`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        this.login({email: this.email, password: this.password})
      }
    },
    watch: {
      currentUser(val) {
        if (val && val.id) {
          this.$notify('success', this.$t('user.success'), this.$t(`user.login-success`), {
            duration: 3000,
            permanent: false
          });
          setTimeout(() => {
            this.$router.push('/')
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
    }
  }
</script>
