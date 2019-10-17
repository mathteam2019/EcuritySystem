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
                <div v-if="submitted && !$v.registerForm.name.required" class="invalid-feedback">
                  {{$t('user.name-field-is-mandatory')}}
                </div>
                <div v-else-if="!$v.registerForm.name.alphaNum" class="invalid-feedback">
                  {{$t('user.name-should-be-numerical-or-characters')}}
                </div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="email" class="form-control" v-model="registerForm.email">
                <span>{{ $t('user.email') }}</span>
                <div v-if="submitted && !$v.registerForm.email.required" class="invalid-feedback">
                  {{$t('user.email-field-is-mandatory')}}
                </div>
                <div v-else-if="!$v.registerForm.email.email" class="invalid-feedback">
                  {{$t('user.please-enter-valid-email') }}
                </div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="password" class="form-control" v-model="registerForm.password">
                <span>{{ $t('user.password') }}</span>
                <div v-if="submitted && !$v.registerForm.password.required" class="invalid-feedback">
                  {{ $t('user.password-field-is-mandatory') }}
                </div>
                <div v-else-if="!$v.registerForm.password.minLength" class="invalid-feedback">
                  {{ $t('user.password-length-need-to-be-more-than-6-letters') }}
                </div>
              </label>
              <label class="form-group has-float-label mb-4">
                <input type="password" class="form-control" v-model="registerForm.confirmPassword">
                <span>{{ $t('user.confirm-password') }}</span>
                <div v-if="submitted && !$v.registerForm.confirmPassword.required" class="invalid-feedback">
                  {{$t('user.password-confirm-field-is-mandatory')}}
                </div>
                <div v-else-if="!$v.registerForm.confirmPassword.sameAsPassword" class="invalid-feedback">
                  {{$t('user.confirmation-should-be-match-as-password')}}
                </div>
              </label>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <router-link tag="a" to="/user/login">{{ $t('menu.login') }}</router-link>
              <b-button type="submit" variant="success" size="lg" :disabled="$v.registerForm.$invalid || processing"
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
  import {validationMixin} from 'vuelidate'
  import {mapGetters, mapActions} from 'vuex'

  const {required, email, minLength, sameAs, alphaNum} = require('vuelidate/lib/validators');

  export default {
    data() {
      return {
        registerForm: {
          name: '',
          email: '',
          password: '',
          confirmPassword: '',
        },
        submitted: false
      }
    },
    computed: {
      ...mapGetters(['currentUser', 'processing', 'registerStatus'])
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
        confirmPassword: {
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
      registerStatus(val) {
        if (val != null) {
          switch (val) {
            case 'invalid-parameter':
              this.$notify('error', this.$t('user.success'), this.$t(`user.register-invalid-parameter`), {
                duration: 3000,
                permanent: false
              });
              break;
            case 'used-email':
              this.$notify('error', this.$t('user.fail'), this.$t(`user.used-email`), {
                duration: 3000,
                permanent: false
              });
              break;
            case 'server-error':
              this.$notify('error', this.$t('user.fail'), this.$t(`user.server-error`), {
                duration: 3000,
                permanent: false
              });
              break;
          }
        }
      }
    },

  }
</script>
