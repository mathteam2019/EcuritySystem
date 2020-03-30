<template>
<div class="sidebar" @mouseenter="isMenuOver=true" @mouseleave="isMenuOver=false" @touchstart="isMenuOver=true">
    <div class="main-menu">
        <vue-perfect-scrollbar class="scroll" :settings="{ suppressScrollX: true, wheelPropagation: false }">
            <ul class="list-unstyled">
                <li v-tooltip.left="$t(item.label)" v-for="(item,index) in menuItems" :class="{ 'active' : (selectedParentMenu === item.id && viewingParentMenu === '') || viewingParentMenu === item.id }" :key="`parent_${item.id}`" :data-flag="item.id">
                    <a   v-if="item.newWindow" :href="item.to" rel="noopener noreferrer" target="_blank">
                        <i :class="item.icon" />
                    </a>
                    <a   v-else-if="item.subs && item.subs.length>0" @click.prevent="openSubMenu($event,item,index)" :href="`#${item.to}`"><i :class="item.icon" />
                        </a>
                    <router-link v-else @click.native="changeSelectedParentHasNoSubmenu(item.id)" :to="item.to"><i :class="item.icon" />
                        </router-link>
                </li>
            </ul>
        </vue-perfect-scrollbar>
    </div>

    <div class="sub-menu" :style="{top:5+ subMenuIndex * 4.1874 + 'rem'}">
        <vue-perfect-scrollbar class="scroll" :settings="{ suppressScrollX: true, wheelPropagation: false }">
            <ul v-for="(item,itemIndex) in menuItems" :class="{'list-unstyled':true, 'd-block' : (selectedParentMenu === item.id && viewingParentMenu === '') || viewingParentMenu === item.id }" :data-parent="item.id" :key="`sub_${item.id}`">
                <li v-for="(sub,subIndex) in item.subs" :class="{'has-sub-item' : sub.subs && sub.subs.length > 0 , 'active' : $route.path.indexOf(sub.to)>-1}">
                    <a v-if="sub.newWindow" :href="sub.to" rel="noopener noreferrer" target="_blank">
                        <i class="icofont-caret-right" />
                        <span>{{ $t(sub.label) }}</span>
                    </a>
                    <template v-else-if="sub.subs &&  sub.subs.length > 0">
                        <b-link v-b-toggle="`menu_${itemIndex}_${subIndex}`" variant="link" class="rotate-arrow-icon opacity-50"><i class="simple-icon-arrow-down"></i> <span class="d-inline-block">{{$t(sub.label)}}</span></b-link>
                        <b-collapse visible :id="`menu_${itemIndex}_${subIndex}`">
                            <ul class="list-unstyled third-level-menu">
                                <li v-for="(thirdLevelSub, thirdIndex) in sub.subs" :key="`third_${itemIndex}_${subIndex}_${thirdIndex}`" :class="{'third-level-menu':true , 'active' : $route.path ===thirdLevelSub.to}">
                                    <a v-if="thirdLevelSub.newWindow" :href="thirdLevelSub.to" rel="noopener noreferrer" target="_blank">
                                      <i class="icofont-caret-right" />
                                        <span>{{ $t(thirdLevelSub.label) }}</span>
                                    </a>

                                    <router-link v-else :to="thirdLevelSub.to">
                                        <i :class="thirdLevelSub.icon" />
                                        <span>{{ $t(thirdLevelSub.label) }}</span>
                                    </router-link>
                                </li>
                            </ul>
                        </b-collapse>
                    </template>
                    <router-link v-else :to="sub.to">
                      <i class="icofont-caret-right" />
                        <span>{{ $t(sub.label) }}</span>
                    </router-link>
                </li>
            </ul>
        </vue-perfect-scrollbar>
    </div>
</div>
</template>

<script>
import {
    mapGetters,
    mapMutations
} from 'vuex'
import {
    menuHiddenBreakpoint,
    subHiddenBreakpoint
} from '../../../constants/config'
import _ from 'lodash'
import Vue from 'vue';
import Tooltip from 'vue-directive-tooltip';
import 'vue-directive-tooltip/dist/vueDirectiveTooltip.css';
import {checkPermissionItemById} from "../../../utils";
import menuTmp from "../../../constants/menu/menu";
Vue.use(Tooltip,{
  delay: 100,
});

export default {
    data() {
        return {
            selectedParentMenu: '',
            isMenuOver: false,
            menuTmp,
            menuItems:null,
            viewingParentMenu: '',
            subMenuIndex:0,
        }
    },
    mounted() {
        this.checkMenuPermission();
        this.selectMenu();
        window.addEventListener('resize', this.handleWindowResize);
        document.addEventListener('click', this.handleDocumentClick);
        this.handleWindowResize();

    },
    beforeDestroy() {
        document.removeEventListener('click', this.handleDocumentClick)
        window.removeEventListener('resize', this.handleWindowResize)
    },

    methods: {
        ...mapMutations(['changeSideMenuStatus', 'addMenuClassname', 'changeSelectedMenuHasSubItems']),

      checkMenuPermission() {
        let tmp = this.menuTmp;
        for(let i=0; i<tmp.length; i++){
          if(tmp[i].permissionId != null){
            if(checkPermissionItemById(tmp[i].permissionId)) {
              tmp = _.without(tmp, tmp[i]);
              i--;
            }else{
              if (tmp[i].subs != null) {
                for(let j=0; j<tmp[i].subs.length; j++){
                  if(tmp[i].subs[j].permissionId!=null){
                    if(checkPermissionItemById(tmp[i].subs[j].permissionId)){
                        tmp[i].subs=_.without(tmp[i].subs, tmp[i].subs[j]);
                      j--;
                    }
                  }
                }
              }
            }
          }
        }
        this.menuItems = tmp;
      },

      selectMenu() {
            const currentParentUrl = this.$route.path.split('/').filter(x => x !== '')[1]
            if (currentParentUrl !== undefined || currentParentUrl !== null) {
                this.selectedParentMenu = currentParentUrl.toLowerCase()
            } else {
                this.selectedParentMenu = 'dashboard'
            }
            this.isCurrentMenuHasSubItem();
        },
        isCurrentMenuHasSubItem() {
            const menuItem = this.menuItems.find(x => x.id === this.selectedParentMenu);
            const isCurrentMenuHasSubItem = menuItem && menuItem.subs && menuItem.subs.length > 0 ? true : false;
            if (isCurrentMenuHasSubItem != this.selectedMenuHasSubItems) {
                if (!isCurrentMenuHasSubItem) {
                    this.changeSideMenuStatus({
                        step: 0,
                        classNames: this.menuType,
                        selectedMenuHasSubItems: false
                    })
                }
            }

            return isCurrentMenuHasSubItem;
        },

        changeSelectedParentHasNoSubmenu(parentMenu) {
            this.selectedParentMenu = parentMenu
            this.viewingParentMenu = parentMenu
            this.changeSelectedMenuHasSubItems(false)
            this.changeSideMenuStatus({
                step: 0,
                classNames: this.menuType,
                selectedMenuHasSubItems: false
            })
        },

        openSubMenu(e, menuItem,index) {
          // if(this.subMenuIndex === index){
          //   //this.sameMenuClickCount
          //   // //this.viewingParentMenu = '';
          //   // this.isMenuOver = false;
          //   // path.map(p => {
          //   //   if (p.nodeName !== 'svg' && p.nodeName !== 'rect' && p.className !== undefined && p.className.indexOf('menu-button') > -1) {
          //   //     cont = false
          //   //   }
          //   // });
          //   this.subMenuIndex = 0;
          // }
          //else {
            //this.isMenuOver = true;
            this.subMenuIndex = index;

            console.log(this.subMenuIndex);
            const selectedParent = menuItem.id;
            const hasSubMenu = menuItem.subs && menuItem.subs.length > 0;
            this.changeSelectedMenuHasSubItems(hasSubMenu);
            if (!hasSubMenu) {
              this.viewingParentMenu = selectedParent;
              this.selectedParentMenu = selectedParent;
              this.toggle();
            } else {
              const currentClasses = this.menuType ?
                this.menuType.split(' ').filter(x => x !== '') :
                '';

              if (!currentClasses.includes('menu-mobile')) {
                if (
                  currentClasses.includes('menu-sub-hidden') &&
                  (this.menuClickCount === 2 || this.menuClickCount === 0)
                ) {
                  this.changeSideMenuStatus({
                    step: 3,
                    classNames: this.menuType,
                    selectedMenuHasSubItems: hasSubMenu
                  });
                } else if (
                  currentClasses.includes('menu-hidden') &&
                  (this.menuClickCount === 1 || this.menuClickCount === 3)
                ) {
                  this.changeSideMenuStatus({
                    step: 2,
                    classNames: this.menuType,
                    selectedMenuHasSubItems: hasSubMenu
                  });
                } else if (
                  currentClasses.includes('menu-default') &&
                  !currentClasses.includes('menu-sub-hidden') &&
                  (this.menuClickCount === 1 || this.menuClickCount === 3)
                ) {
                  this.changeSideMenuStatus({
                    step: 0,
                    classNames: this.menuType,
                    selectedMenuHasSubItems: hasSubMenu
                  });
                }
              } else {

                this.addMenuClassname({
                  classname: 'sub-show-temporary',
                  currentClasses: this.menuType
                });
              }
              this.viewingParentMenu = selectedParent;
            }
          //}
        },
        handleDocumentClick(e) {
          //console.log(e.path, e.composedPath, e.composedPath(), this.isMenuOver);
            if (!this.isMenuOver) {
                let cont = true
                var path = e.path || (e.composedPath && e.composedPath())
              console.log(path);

                path.map(p => {
                    if (p.nodeName !== 'svg' && p.nodeName !== 'rect' && p.className !== undefined && p.className.indexOf('menu-button') > -1) {
                        cont = false
                    }
                });

                this.viewingParentMenu = '';
                this.selectMenu();
                if (cont || !this.selectedMenuHasSubItems) {
                    this.toggle()
                }
            }
        },
        toggle() {
          console.log("toggle")
            const currentClasses = this.menuType.split(' ').filter(x => x !== '')
            if (currentClasses.includes('menu-sub-hidden') && this.menuClickCount === 3) {
                this.changeSideMenuStatus({
                    step: 2,
                    classNames: this.menuType,
                    selectedMenuHasSubItems: this.selectedMenuHasSubItems
                })
            } else if (currentClasses.includes('menu-hidden') || currentClasses.includes('menu-mobile')) {
                if (!(this.menuClickCount === 1 && !this.selectedMenuHasSubItems)) {
                    this.changeSideMenuStatus({
                        step: 0,
                        classNames: this.menuType,
                        selectedMenuHasSubItems: this.selectedMenuHasSubItems
                    })
                }
            }
        },
        // Resize
        handleWindowResize(event) {

            if (event && !event.isTrusted) {
                return
            }
            let nextClasses = this.getMenuClassesForResize(this.menuType);
            this.changeSideMenuStatus({
                step: 0,
                classNames: nextClasses.join(' '),
                selectedMenuHasSubItems: this.selectedMenuHasSubItems
            })
        },
        getMenuClassesForResize(classes) {

            let nextClasses = classes.split(' ').filter(x => x !== '');
            const windowWidth = window.innerWidth;

            if (windowWidth < menuHiddenBreakpoint) {
                nextClasses.push('menu-mobile')
            } else if (windowWidth < subHiddenBreakpoint) {
                nextClasses = nextClasses.filter(x => x !== 'menu-mobile');
                if (
                    nextClasses.includes('menu-default') &&
                    !nextClasses.includes('menu-sub-hidden')
                ) {
                    nextClasses.push('menu-sub-hidden')
                }
            } else {
                nextClasses = nextClasses.filter(x => x !== 'menu-mobile');
                if (
                    nextClasses.includes('menu-default') &&
                    nextClasses.includes('menu-sub-hidden')
                ) {
                    nextClasses = nextClasses.filter(x => x !== 'menu-sub-hidden')
                }
            }
            return nextClasses
        },
    },
    computed: {
        ...mapGetters({
            menuType: 'getMenuType',
            menuClickCount: 'getMenuClickCount',
            selectedMenuHasSubItems: 'getSelectedMenuHasSubItems'
        })
    },
    watch: {
        '$route'(to, from) {
            if (to.path !== from.path) {

                const toParentUrl = to.path.split('/').filter(x => x !== '')[1];
                if (toParentUrl !== undefined || toParentUrl !== null) {
                    this.selectedParentMenu = toParentUrl.toLowerCase()
                } else {
                    this.selectedParentMenu = 'dashboard'
                }
                // this.isCurrentMenuHasSubItem();
                this.selectMenu();
                this.toggle();
                this.changeSideMenuStatus({
                    step: 0,
                    classNames: this.menuType,
                    selectedMenuHasSubItems: this.selectedMenuHasSubItems
                });

                window.scrollTo(0, top)
            }
        }
    }
}
</script>
