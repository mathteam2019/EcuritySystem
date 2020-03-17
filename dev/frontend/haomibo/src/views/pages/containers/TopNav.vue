<style lang="scss">
  nav.navbar {
    .dropdown-menu-item-hidden {
      ul.dropdown-menu {
        opacity: 0;
        display: none !important;
      }
    }
  }

  #modal-logout___BV_modal_content_ {
    position: relative;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    width: 90%;
    pointer-events: auto;
    background-color: #fff;
    background-clip: padding-box;
    border: 1px solid rgba(0, 0, 0, 0.2);
    border-radius: .3rem;
    outline: 0;
  }


</style>
<template>
  <nav class="navbar fixed-top">
    <div class="d-flex align-items-center navbar-left">
      <a href="#" class="menu-button d-none d-md-block" v-if="false"
         @click.prevent="changeSideMenuStatus({step :menuClickCount+1,classNames:menuType,selectedMenuHasSubItems})">
        <menu-icon/>
      </a>
      <a href="#" class="menu-button-mobile d-xs-block d-sm-block d-md-none" v-if="false"
         @click.prevent="changeSideMenuForMobile(menuType)">
        <mobile-menu-icon/>
      </a>


    </div>

    <router-link class="navbar-logo" tag="a" to="/">
      <span class="logo d-none d-xs-block"/>
      <span class="logo-mobile d-block d-xs-none"/>
    </router-link>
    <router-link class="navbar-title" tag="a" to="/">
      <span class="logo d-none d-xs-block" style="font-size: 2rem;color: white">{{$t('dashboard.title')}}</span>
      <span class="logo-mobile d-block d-xs-none"/>
    </router-link>

    <div class="navbar-right">
      <div class="d-inline-block mr-2">
        <b-dropdown id="langddm" variant="empty" size="sm" toggle-class="language-button" class="mr-4">
          <template slot="button-content">
            <span>
                <img class="locale" :alt="$i18n.locale.toUpperCase()" :src="getLocaleIcon()"/>
            </span>
            <span class="name ml-2 mr-3">{{$i18n.locale.toUpperCase()}}</span>
          </template>
          <b-dropdown-item v-for="(l,index) in localeOptions" :key="index" @click="changeLocale(l)">
            {{l.name}}
          </b-dropdown-item>
        </b-dropdown>
      </div>
      <div class="user d-inline-block">
        <b-dropdown class="dropdown-menu-right dropdown-menu-item-hidden" right variant="empty" toggle-class="p-0"
                    menu-class="mt-3" no-caret>
          <template slot="button-content" v-if="currentUser">
            <span @click="showPasswordResetView()">
                <img :alt="currentUser.title" :src="this.portrait" @error="portraitOnError"/>
            </span>
            <span @click="showPasswordResetView()" class="name ml-1 mr-2">{{currentUser.name}}</span>
          </template>
          <b-dropdown-item @click="showPasswordResetView()">{{this.$t('menu.account')}}</b-dropdown-item>
        </b-dropdown>
      </div>
      <div class="d-inline-block">
        <img src="/assets/img/turn_on_icon.svg" class="ml-5 mb-1 logout" @click="confirmLogout"/>
      </div>
    </div>
    <b-modal centered id="modal-reset" ref="modal-reset">
      <template slot="modal-header">
        <h2 style="font-size: 1.7rem; font-weight: bold;" class="modal-title">
          {{$t('password-reset.password-change')}}</h2>
        <button type="button" aria-label="Close" @click="hideModal('modal-reset')" class="close">×</button>
        <!--        <h3 class="text-center font-weight-bold">{{$t('password-reset.password-change')}}</h3>-->
      </template>
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <div style="width: 100%">
            <!--            <b-form-group class="mw-100">-->
            <!--              <template slot="label">{{$t('password-reset.user-account')}}<span-->
            <!--                class="text-danger"/>-->
            <!--              </template>-->
            <!--              <b-form-input disabled v-model="passwordForm.userAccount" class="mw-100"/>-->
            <!--            </b-form-group>-->
            <b-form-group class="mw-100">
              <template slot="label">{{$t('password-reset.old-password')}}<span
                class="text-danger">*</span>
              </template>
              <b-form-input type="password" v-model="passwordForm.oldPassword"
                            :state="passwordForm.oldPassword!==null && invalidPassword && !$v.passwordForm.oldPassword.$invalid"
                            class="mw-100"/>
            </b-form-group>
            <b-form-group class="mw-100">
              <template slot="label">{{$t('password-reset.new-password')}}<span
                class="text-danger">*</span>
              </template>
              <b-form-input type="password" v-model="passwordForm.password"
                            :state="!$v.passwordForm.password.$dirty ? null : !$v.passwordForm.password.$invalid"
                            class="mw-100"/>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('password-reset.confirm-password')}}<span
                class="text-danger">*</span>
              </template>
              <b-form-input type="password" v-model="passwordForm.confirmPassword"
                            :state="!$v.passwordForm.confirmPassword.$dirty ? null : !$v.passwordForm.confirmPassword.$invalid"
                            class="mw-100"/>
            </b-form-group>
          </div>
        </b-col>
      </b-row>
      <template slot="modal-footer">
        <b-button variant="primary default" @click="savePassword" class="mr-1">
          {{$t('password-reset.confirm')}}
        </b-button>
        <b-button variant="light default" @click="hideModal('modal-reset')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-logout" ref="modal-logout" class="modal-logout-content">
      <template slot="modal-header">
        <h2 style="font-size: 1.7rem; font-weight: bold;" class="modal-title">{{$t('logout.title')}}</h2>
        <button type="button" aria-label="Close" @click="hideModal('modal-logout')" class="close">×</button>
      </template>
      <span style="font-size: 1rem; font-weight: bold;">{{$t('logout.body')}}
      </span>
      <template slot="modal-footer">
        <b-button variant="primary default" @click="logout" class="mr-1">
          {{$t('password-reset.confirm')}}
        </b-button>
        <b-button variant="light default" @click="hideModal('modal-logout')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </nav>

</template>

<script>
  import Switches from 'vue-switches'
  import notifications from '../../../data/notifications'

  import {mapActions, mapGetters, mapMutations} from 'vuex'
  import {MenuIcon, MobileMenuIcon} from '../../../components/Svg'
  import {apiBaseUrl, defaultColor, localeOptions, menuHiddenBreakpoint} from '../../../constants/config'
  import {getDirection, removeLoginInfo, saveLanguageInfo, setLocale, setDirection} from '../../../utils'
  import {getApiManager, isAccountValid} from "../../../api";
  import {responseMessages} from "../../../constants/response-messages";
  import {validationMixin} from 'vuelidate';
  import VuejsDialog from 'vuejs-dialog';

  const {required, minLength, sameAs} = require('vuelidate/lib/validators');
  export default {
    components: {
      'menu-icon': MenuIcon,
      'mobile-menu-icon': MobileMenuIcon,
      'switches': Switches
    },
    mixins: [validationMixin],
    validations: {
      passwordForm: {
        oldPassword: {
          required, minLength: minLength(6)
        },
        password: {
          required, minLength: minLength(6),
          isAccountValid
        },
        confirmPassword: {
          required, sameAs: sameAs('password')
        },
      }
    },
    data() {
      return {
        selectedParentMenu: '',
        menuHiddenBreakpoint,
        localeOptions,
        notifications,
        portrait: '',
        invalidPassword:true,
        passwordForm: {
          oldPassword: null,
          password: null,
          confirmPassword: null
        }
      }
    },
    mounted() {
      this.portrait = `${this.currentUser.portrait}`;
      this.setLanguageInfo();
      //this.passwordForm.userAccount = `${this.currentUser.name}`;
    },
    methods: {
      ...mapMutations(['changeSideMenuStatus', 'changeSideMenuForMobile']),
      ...mapActions(['setLang']),

      showPasswordResetView() {
        this.passwordForm = {
          oldPassword: null,
          password: null,
          confirmPassword: null
        };
        this.$refs['modal-reset'].show();
      },
      confirmLogout() {
        this.$refs['modal-logout'].show();
      },
      savePassword() {
        this.$v.passwordForm.$touch();
        if (this.$v.passwordForm.$invalid) {

          if(this.passwordForm.password === null||this.passwordForm.password===''){
            this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.input-none`), {
              duration: 3000,
              permanent: false
            });
          }
          else {
            if(!isAccountValid(this.passwordForm.password)){
              this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.format-invalid`), {
                duration: 3000,
                permanent: false
              });
            }
            else if(this.$v.passwordForm.confirmPassword.$invalid){
              this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.confirm-invalid`), {
                duration: 3000,
                permanent: false
              });
            }
          }
          return;
        }
        // this.passwordForm.password
        getApiManager()
          .post(`${apiBaseUrl}/auth/change-password`, {
            oldPassword: this.passwordForm.oldPassword,
            password: this.passwordForm.password
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`password-reset.update-password-successful`), {
                  duration: 3000,
                  permanent: false
                });
                this.$refs['modal-reset'].hide();
                this.passwordForm.oldPassword = null;
                this.passwordForm.password = null;
                this.passwordForm.confirmPassword = null;
                this.invalidPassword=true;
                this.logout();
                break;
              case responseMessages['invalid-password']:
                this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.invalid-password`), {
                  duration: 3000,
                  permanent: false
                });
                this.invalidPassword =false;
                break;
            }
          })
          .catch((error) => {
          });
      },
      hideModal(modal) {
        this.$refs[modal].hide();
      },
      portraitOnError(e) {
        this.portrait = '/assets/img/user_placeholder.png';
      },
      setLanguageInfo(){
        setLocale(this.$i18n.locale);

      },
      changeLocale(l) {
        let locale = l.id;
        let direction = l.direction;
        const currentDirection = getDirection().direction;
        if (direction !== currentDirection) {
          setDirection(direction)
        }
        //saveLanguageInfo();

        this.setLang(locale)

      },

      getLocaleIcon() {
        const locale = this.$i18n.locale;
        for (let l of localeOptions) {
          if (l.id === locale) return l.icon;
        }
        return localeOptions[1].icon;
      },

      logout() {

        this.hideModal('modal-logout');

        getApiManager()
          .post(`${apiBaseUrl}/auth/logout`, {})
          .then(response => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                saveLanguageInfo();

                removeLoginInfo();


                this.$router.push('/auth/login');

                break;

            }
          })
          .catch((error) => {

          });
      },

      getThemeColor() {
        return localStorage.getItem('themeColor') ?
          localStorage.getItem('themeColor') :
          defaultColor
      },
    },
    computed: {
      ...mapGetters({
        currentUser: 'currentUser',
        menuType: 'getMenuType',
        menuClickCount: 'getMenuClickCount',
        selectedMenuHasSubItems: 'getSelectedMenuHasSubItems'
      })
    },
    beforeDestroy() {
    },
    created() {
      const color = this.getThemeColor()
    },
    watch: {
      '$i18n.locale'(to, from) {
        if (from !== to) {
          this.$router.go(this.$route.path)
        }
      },
      'passwordForm.oldPassword': function (newVal) {
        this.invalidPassword = true;
      },
    }
  }
</script>
