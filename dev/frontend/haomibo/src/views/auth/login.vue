<style lang="scss">
  $text-color: #d4d5da;
  .auth-login-page {

    .header-title {
      font-size: 4.375rem;
      font-weight: bold;
      text-shadow: 3px 4px 0px black;
    }

    .form-control {
      max-width: unset !important;
    }

    img.logo {
      top: 3.125rem;
      left: 3.125rem;
    }

    footer.auth-login {
      position: absolute;
      bottom: 10%;
      left: 15%;
      color: #fffefe;
    }

    .line-form {
      #langddm {
        width: 100%;
        border-bottom: solid 1px white;
        border-right: transparent;
        border-left: transparent;
        border-top: transparent;

        .dropdown-toggle::after {
          background: none !important;
        }

        span.front-icon {
          height: 2.25rem;
          line-height: 2.25rem;
          position: absolute;
          left: 0;
          top: 1px;

          img {
            width: 1.1875rem;
          }
        }

        span.name {
          padding-left: 1.125rem;
          color: $text-color;
        }
      }

      div.input-group {
        span.front-input-icon {
          height: 2.25rem;
          line-height: 2.25rem;
          color: $text-color;
        }

        .form-control {
          padding-left: 2.5rem;
          border-top: transparent;
          border-left: transparent;
          border-right: transparent;
          border-bottom-right-radius: unset;
          outline: none;
          background: transparent !important;
          color: white !important;
          font-size: 1rem;

          &::placeholder {
            color: $text-color;
          }

          &:-webkit-autofill {
            -webkit-text-fill-color: $text-color !important;
          }
        }
      }

      button.btn-primary {
        background-color: #1782d4 !important;

        &:hover {
          background-color: darken(#1782d4, 8%);
        }
      }
    }
  }

  body.rtl {
    .fixed-background {
      transform: rotateY(180deg);
    }

    .auth-login-page {
      img.logo {
        top: 3.125rem;
        left: 3.125rem;
      }

      footer.auth-login {
        position: absolute;
        bottom: 10%;
        right: 15%;
        color: #fffefe;
      }

      .line-form {
        #langddm {
          width: 100%;
          border-bottom: solid 1px white;
          border-right: transparent;
          border-left: transparent;
          border-top: transparent;

          .dropdown-toggle::after {
            background: none !important;
          }

          span.front-icon {
            height: 2.25rem;
            line-height: 2.25rem;
            position: absolute;
            right: 0;
            top: 1px;

            img {
              width: 1.1875rem;
            }
          }

          span.name {
            padding-right: 1.125rem;
            color: $text-color;;
          }
        }

        button.btn-primary {
          background-color: #1782d4 !important;

          &:hover {
            background-color: darken(#1782d4, 8%);
          }
        }

        div.input-group {
          span.front-input-icon {
            height: 2.25rem;
            line-height: 2.25rem;
          }

          .form-control {
            padding-right: 2.5rem;
            border-top: transparent;
            border-left: transparent;
            border-right: transparent;
            border-bottom-right-radius: unset;
            outline: none;
            background: transparent !important;
            color: $text-color;
            font-size: 1rem;
          }
        }
      }
    }

  }

  @media all and (-ms-high-contrast: none), (-ms-high-contrast: active) {
    .auth-content-only-ie {
      margin-top: 10% !important;
    }
    .auth-login-page {
      .form-control {
        max-width: 100% !important;
        border-radius: 0px !important;
      }
    }
  }


</style>

<template>
  <b-row class="h-100 auth-login-page">
    <img class="position-absolute logo" src="../../assets/img/logo.png"/>
    <b-col md=10 class="mx-auto my-auto auth-content-only-ie">
      <h2 class="mb-5 text-white header-title">{{$t('login.title')}}</h2>
      <b-row class="auth-card ">
        <b-col cols="9">
          <div class="form-side line-form">
            <b-form @submit.prevent="formSubmit">
              <div class="form-group mb-5">
                <div class="input-group ">
                  <span class="front-input-icon position-absolute "><i style="font-size: 18px"
                                                                       class="icofont-ui-user"/> </span>
                  <input type="text" class="form-control" :placeholder="$t('login.enter-user-email')" v-model="account"
                         autocomplete="off">
                </div>
              </div>
              <div class="form-group mb-5">
                <div class="input-group ">
                  <span class="front-input-icon position-absolute "><i style="font-size: 20px"
                                                                       class="icofont-unlock"/> </span>
                  <input type="password" class="form-control" :placeholder="$t('login.enter-user-password')"
                         v-model="password" autocomplete="off">
                </div>
              </div>
              <b-dropdown id="langddm" variant="empty" class="mb-4" toggle-class="language-button">
                <template slot="button-content">
                <span class="front-icon">
                <img class="locale" :alt="$i18n.locale.toUpperCase()" :src="getLocaleIcon()"/>
                </span>
                  <span class="name float-left">{{fnGetLangName($i18n.locale)}}</span>
                </template>
                <b-dropdown-item v-for="(l,index) in localeOptions" :key="index" @click="changeLocale(l)">
                  {{l.name}}
                </b-dropdown-item>
              </b-dropdown>
              <div class=" mt-4">
                <b-button type="submit" variant="primary default" class="btn-block text-center" size="lg"
                          :disabled="processing">{{
                  $t('user.login-button')}}
                </b-button>
              </div>
            </b-form>
          </div>
        </b-col>
      </b-row>
    </b-col>
    <footer class="auth-login">Copyright by JINUNG Company limited 2019.All right Reserved</footer>
  </b-row>
</template>
<script>
  import {mapActions, mapGetters} from 'vuex';
  import {apiBaseUrl, localeOptions} from '../../constants/config'
  import {
    getDirection,
    saveLoginInfo,
    scheduleRefreshToken,
    setDirection,
    setInvalidCount, getInvalidCount, removeCount,
    saveDicDataGroupByDicId, savePermissionInfo, savePermissionInfoId
  } from '../../utils'
  import {getApiManager} from "../../api";
  import {responseMessages} from "../../constants/response-messages";


  export default {
    data() {
      return {
        account: '',
        password: '',
        count: null,
        localeOptions,
        processing: false
      }
    },
    computed: {
      ...mapGetters(['currentUser'])
    },
    methods: {
      ...mapActions(['setCurrentUser', 'setLang']),
      changeLocale(l) {
        let locale = l.id;
        let direction = l.direction;
        const currentDirection = getDirection().direction;
        if (direction !== currentDirection) {
          setDirection(direction)
        }

        this.setLang(locale)
      },
      fnGetLangName(value) {
        let langName = '';
        this.localeOptions.forEach((locale) => {
          if (locale.id === value) {
            langName = locale.name;
            return '';
          }
        });
        return langName;
      },
      getLocaleIcon() {
        const locale = this.$i18n.locale;
        for (let l of localeOptions) {
          if (l.id === locale) return l.icon;
        }
        return localeOptions[1].icon;
      },
      formSubmit() {
        this.count = getInvalidCount(this.account);
        console.log(this.count);

        if (this.account.length === 0) {
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

        getApiManager()
          .post(`${apiBaseUrl}/auth/login`, {
            userAccount: this.account,
            password: this.password,
            count: this.count
          })
          .then(response => {
            this.processing = false;
            let message = response.data.message;
            let data = response.data.data;
            //let codeData = response.data.data.
            switch (message) {
              case responseMessages['ok']:
                if (data.user.category !== 'normal' && data.user.category !== 'admin') {
                  this.$notify('success', this.$t('user.login-fail'), this.$t(`login.not-normal-role`), {
                    duration: 3000,
                    permanent: false
                  });
                  return;
                }
                saveLoginInfo(data);
                savePermissionInfo(data.permission);
                savePermissionInfoId(data.permission);
                scheduleRefreshToken();
                saveDicDataGroupByDicId(data);
                removeCount(this.account);

                this.$notify('success', this.$t('user.success'), this.$t(`user.login-success`), {
                  duration: 3000,
                  permanent: false
                });

                this.setCurrentUser({
                  ...this.currentUser,
                  ...data.user
                });

                if (data.user.category === 'normal') {
                  this.$router.push('/pages/dashboard');
                  break;
                }

                if (data.user.category === 'admin') {
                  this.$router.push('/pages/dashboard');
                  break;
                }

                break;
              case responseMessages['invalid-parameter']:
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.invalid-parameter`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['user-not-found']:
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.user-not-found`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['invalid-password']:
                setInvalidCount(this.account);
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.invalid-password`), {
                  duration: 3000,
                  permanent: false
                });
                break;

              case responseMessages['pre-user-pending-status']:
                setInvalidCount(this.account);
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.forbidden-warning`), {
                  duration: 3000,
                  permanent: false
                });
                break;

              case responseMessages['user-pending-status']:
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.forbidden`), {
                  duration: 3000,
                  permanent: false
                });
                removeCount(this.account);
                break;

              case responseMessages['user-non-active-status']:
                this.$notify('error', this.$t(`user.login-fail`), this.$t(`response-messages.inactive`), {
                  duration: 3000,
                  permanent: false
                });
                removeCount(this.account);
                break;
            }
          })
          .catch((error) => {
            this.processing = false;
          });

      },
    },
    watch: {
      '$i18n.locale'(to, from) {
        if (from !== to) {
          this.$router.go(this.$route.path)
        }
      },
    }
  }
</script>
