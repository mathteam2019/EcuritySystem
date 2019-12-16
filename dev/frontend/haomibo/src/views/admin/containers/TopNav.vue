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

    <router-link class="navbar-logo" tag="a" to="/app">
      <span class="logo d-none d-xs-block"></span>
      <span class="logo-mobile d-block d-xs-none"></span>
    </router-link>
    <router-link class="navbar-title" tag="a" to="/app">
      <span class="logo d-none d-xs-block"></span>
      <span class="logo-mobile d-block d-xs-none"></span>
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
        <b-dropdown class="dropdown-menu-right" right variant="empty" toggle-class="p-0" menu-class="mt-3" no-caret>
          <template slot="button-content" v-if="currentUser">
            <span>
                <img :alt="currentUser.title" :src="portrait" @error="portraitOnError"/>
            </span>
            <span class="name ml-1 mr-2">{{currentUser.name}}</span>
          </template>
          <b-dropdown-item>{{this.$t('menu.account')}}</b-dropdown-item>
        </b-dropdown>
      </div>
      <div class="d-inline-block">
        <img src="/assets/img/turn_on_icon.svg" class="ml-5 mb-1 logout" @click="logout"/>
      </div>
    </div>
  </nav>
</template>

<script>
  import Switches from 'vue-switches'
  import notifications from '../../../data/notifications'

  import {mapActions, mapGetters, mapMutations} from 'vuex'
  import {MenuIcon, MobileMenuIcon} from '../../../components/Svg'
  import {apiBaseUrl, defaultColor, localeOptions, menuHiddenBreakpoint} from '../../../constants/config'
  import {getDirection, removeLoginInfo, setDirection} from '../../../utils'
  import {getApiManager} from "../../../api";
  import {responseMessages} from "../../../constants/response-messages";

  export default {
    components: {
      'menu-icon': MenuIcon,
      'mobile-menu-icon': MobileMenuIcon,
      'switches': Switches
    },
    data() {
      return {
        selectedParentMenu: '',
        menuHiddenBreakpoint,
        localeOptions,
        notifications,
        portrait: ''
      }
    },
    mounted () {
      this.portrait = `${apiBaseUrl}${this.currentUser.portrait}`;
    },
    methods: {
      ...mapMutations(['changeSideMenuStatus', 'changeSideMenuForMobile']),
      ...mapActions(['setLang']),

      portraitOnError(e) {
        this.portrait = '/assets/img/user_placeholder.png';
      },
      changeLocale(l) {
        let locale = l.id;
        let direction = l.direction;
        const currentDirection = getDirection().direction;
        if (direction !== currentDirection) {
          setDirection(direction)
        }

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

        return getApiManager()
          .post(`${apiBaseUrl}/auth/logout`, {})
          .then(response => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:

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
    }
  }
</script>
