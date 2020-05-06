<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-card v-show="!isLoading" class="main-without-tab">
        <b-row class="h-100">
          <b-col cols="9" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="2" class="pr-3">
                  <b-form-group>
                    <template slot="label">{{$t('system-setting.goods')}}</template>
                    <b-form-input v-model="goods"/>
                  </b-form-group>
                </b-col>
                <b-col cols="10" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="searchGoodss()">
                      <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="resetGoodsSearchForm()">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onClickCreateGoods()" :disabled="checkPermItem('seized_good_create')" variant="success default">
                      <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>

              <b-row class="flex-grow-1 m-0">
                <b-col cols="12">
                  <div class="table-wrapper table-responsive">
                    <div v-show="loadingTable" class="overlay flex flex-column items-center justify-center">
                      <div class="loading_table"></div>
                    </div>
                    <vuetable
                      ref="vuetable"
                      :fields="vuetableItems.fields"
                      :api-url="vuetableItems.apiUrl"
                      :http-fetch="vuetableHttpFetch"
                      :per-page="vuetableItems.perPage"
                      track-by="goodsId"
                      pagination-path="data"
                      data-path="data.data"
                      class="table-hover"
                      @vuetable:pagination-data="onGoodsPaginationData"
                      @vuetable:loading="loadingTable = true"
                      @vuetable:loaded="loadingTable = false"
                    >
                      <template slot="seizedGoods" slot-scope="props">
                      <span v-if="checkPermItem('seized_good_modify')" class="cursor-p text-primary">
                        {{props.rowData.seizedGoods}}
                      </span>
                        <span v-else class="cursor-p text-primary" @click="onGoodsNumberClicked(props.rowData, true)">
                        {{props.rowData.seizedGoods}}
                      </span>
                      </template>
                      <template slot="operating" slot-scope="props">
                        <b-button
                          size="sm" :disabled="checkPermItem('seized_good_modify')"
                          variant="primary default btn-square" @click="onGoodsNumberClicked(props.rowData)">
                          <i class="icofont-edit"/>
                        </b-button>
                        <b-button size="sm" variant="danger default btn-square" class="m-0" :disabled="checkPermItem('seized_good_delete')"
                                  @click="onClickDeleteGoods(props.rowData)">
                          <i class="icofont-bin"/>
                        </b-button>
                      </template>

                    </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="goodsPagination"
                      @vuetable-pagination:change-page="onGoodsChangePage"
                      :initial-per-page="vuetableItems.perPage"
                      @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                    />
                  </div>
                </b-col>
              </b-row>
            </div>
          </b-col>
          <b-col cols="3" class="pl-0" v-if="selectedGoods || goodsForm.visible">
            <div class="section d-flex flex-column h-100 px-3">
              <div v-if="selectedGoods || goodsForm.visible" style="margin-left: 1.5rem; margin-right: 1.5rem;" class="flex-grow-1">
                <b-form class="h-100 d-flex flex-column">
                  <b-form-group>
                    <template slot="label">
                      {{$t('system-setting.goods')}}
                      <span class="text-danger">*</span>
                    </template>
<!--                    <b-form-input-->
<!--                      v-model="goodsForm.goodsName"-->
<!--                      :state="!$v.goodsForm.goodsName.$invalid"-->
<!--                      :placeholder="$t('system-setting.enter-goods')"/>-->
                    <b-form-select
                      v-model="goodsForm.goodsName" :options="onNameOptions"
                      :disabled="showable"
                      :state="!$v.goodsForm.goodsName.$invalid"
                    />
                  </b-form-group>

                  <b-form-group>
                    <template slot="label">
                      {{$t('system-setting.goods-grade')}}
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-select
                      v-model="goodsForm.goodsGrade" :options="onGradeOptions"
                      :disabled="showable"
                      :state="!$v.goodsForm.goodsGrade.$invalid"
                      />
                  </b-form-group>

                  <b-form-group>
                    <template slot="label">
                      {{$t('system-setting.goods-category')}}
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-select
                      v-model="goodsForm.goodsCategory" :options="onCategoryOptions"
                      :disabled="showable"
                      :state="!$v.goodsForm.goodsCategory.$invalid"
                    />
                  </b-form-group>

                  <div class="d-flex align-items-end justify-content-end pt-3">
                    <div>
                      <b-button :disabled="showable" @click="onClickSaveGoods" size="sm" variant="info default" class="mr-3">
                        <i class="icofont-save"/>
                        {{$t('permission-management.permission-control.save')}}
                      </b-button>
                    </div>
                  </div>

                </b-form>
              </div>
            </div>
          </b-col>
        </b-row>
    </b-card>

    <div v-show="isLoading" class="loading"></div>

    <b-modal centered id="modal-delete-goods" ref="modal-delete-goods"
             title="物被删除">
      您确定要删除扣押的货物吗？
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" @click="deleteGoods" class="mr-1">{{$t('system-setting.ok')}}</b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-goods')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<style>

  .col-form-label {
    margin-bottom: 1px;
  }
  .section {
    border-radius: 0.3125rem;
    border-top-left-radius: 0.3125rem;
    border-top-right-radius: 0.3125rem;
    border-bottom-right-radius: 0.3125rem;
    border-bottom-left-radius: 0.3125rem;
    padding: 1.5rem 0 1rem 0;
    border: solid 1px #cccccc;
  }
  .halo-tree .inputCheck {
    top: 2px !important;
  }

  .tb-icon {
    font-size: 20px;
  }

  .tb-button {
    font-size: 20px;
    cursor: pointer;
  }

  .tb-button-disabled {
    font-size: 20px;
    color: lightgray !important;
  }

  span.cursor-p {
    cursor: pointer !important;
  }

  .h-35vh {
    height: 32vh;
    max-height: 33vh;
    overflow: auto;
  }
</style>
<style lang="scss">
  .loading_table {
    display: inline-block;
    width: 30px;
    height: 30px;
    border: 2px solid rgba(#145388, 0.2);
    border-radius: 50%;
    border-top-color: #145388;
    animation: spin 1s ease-in-out infinite;
    -webkit-animation: spin 1s ease-in-out infinite;
    left: calc(38% - 15px);
    top: calc(50% - 15px);
    position: fixed;
    z-index: 1;
  }
  .search-form-group {
    [goods="group"] {
      position: relative;

      .form-control {
        padding-right: 30px;
      }

      .search-input-icon {
        position: absolute;
        top: 50%;
        right: 1em;
        transform: translateY(-50%);
      }

    }

  }
</style>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import axios from 'axios'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import {checkPermissionItem, getDirection, savePermissionInfo, getLocale} from "../../../utils";
  import _ from "lodash";
  import {validationMixin} from 'vuelidate';

  const {required} = require('vuelidate/lib/validators');
  import {responseMessages} from '../../../constants/response-messages';

  import staticUserTableData from '../../../data/user'
  import {
    downLoadFileFromServer,
    getApiManager,
    getApiManagerError,
    getDateTimeWithFormat,
    printFileFromServer
  } from "../../../api";

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree
    },
    mounted() {
      this.tableData = staticUserTableData;
      this.getOptions();
    },
    mixins: [validationMixin],
    data() {
      return {
        isLoading: false,
        loadingTable:false,
        showable:false,
        goodsForm: {
          visible: false,
          goodsName: '',
          goodsGrade: '',
          goodsCategory: ''
        },

        onCategoryData:[],
        onNameData:[],
        onGradeData:[],
        onNameOptions :[],
        onCategoryOptions:[],
        onGradeOptions:[],

        onCategoryOption: [
          {value: '1000001401', text: '枪支'},
          {value: '1000001402', text: '药品'},
          {value: '1000001403', text: '其他'},
        ],
        onGradeOption: [
          {value: '1000001501', text: '1级'},
          {value: '1000001502', text: '2级'},
          {value: '1000001503', text: '3级'},
          {value: '1000001504', text: '4级'},
          {value: '1000001505', text: '5级'},
        ],
        isSelectedAllResourcesForGoodsForm: false,
        currentResourceTreeDataForGoodsForm: [],
        goods: '',
        resourceList: [],
        resourceTreeData: [],
        selectedGoods: null,
        deletingGoods: null,
        isSelectedAllResourcesForGoods: false,
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/seized-good-management/seized/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.permission-control.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:seizedGoods',
              title: this.$t('system-setting.goods'),
              sortField: 'seizedGoods',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '19%'
            },
            {
              name: 'seizedGoodType',
              title: this.$t('system-setting.goods-category'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '19%',
              callback: (seizedGoodType) => {
                if (seizedGoodType == null) return '';
                return this.getGoodsCategory(seizedGoodType);
              }
            },
            {
              name: 'seizedGoodsLevel',
              title: this.$t('system-setting.goods-grade'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%',
              callback: (seizedGoodsLevel) => {
                if (seizedGoodsLevel == null) return '';
                return this.getGoodsGrade(seizedGoodsLevel);
              }
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ]
        },
      }
    },
    validations: {
      goodsForm: {
        goodsName: {
          required
        },
        goodsGrade: {
          required
        },
        goodsCategory: {
          required
        }
      },
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.selectedGoods =false;
        this.goodsForm.visible =false;
      },

      onNameData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let options = [];
        options = newVal.map(site => ({
          text: site.dataValue,
          value: site.dataCode
        }));
        this.onNameOptions = options;
      },
      onGradeData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let options = [];
        options = newVal.map(site => ({
          text: site.dataValue,
          value: site.dataCode
        }));
        this.onGradeOptions = options;
      },
      onCategoryData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let options = [];
        options = newVal.map(site => ({
          text: site.dataValue,
          value: site.dataCode
        }));
        this.onCategoryOptions = options;
      },
    },
    methods: {

      getOptions(){
        getApiManagerError()
          .post(`${apiBaseUrl}/dictionary-management/dictionary-data/get-by-id`, {
          'dictionaryId': 14
        })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                  this.onCategoryData = data;
                break;

            }
          })
          .catch((error) => {
          });

        getApiManagerError()
          .post(`${apiBaseUrl}/dictionary-management/dictionary-data/get-by-id`, {
            'dictionaryId': 15
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                this.onGradeData = data;
                break;
            }
          })
          .catch((error) => {
          });

        getApiManagerError()
          .post(`${apiBaseUrl}/dictionary-management/dictionary-data/get-by-id`, {
            'dictionaryId': 16
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                this.onNameData = data;
                break;
            }
          })
          .catch((error) => {
          });
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      getGoodsCategory(value){
        if(value==null)
          return null;
        for(let i=0; i<this.onCategoryOptions.length; i++){
          if(this.onCategoryOptions[i].value===value)
            return this.onCategoryOptions[i].text;
        }
      },
      getGoodsGrade(value){
        if(value==null)
          return null;
        for(let i=0; i<this.onGradeOptions.length; i++){
          if(this.onGradeOptions[i].value===value)
            return this.onGradeOptions[i].text;
        }
      },

      getDataCodeValue(value){
        const dictionary = {

          "1000001401": `枪支`,
          "1000001402": `药品`,
          "1000001403": `其他`,
          "1000001501": `1级`,
          "1000001502": `2级`,
          "1000001503": `3级`,
          "1000001504": `4级`,
          "1000001505": `5级`,
        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      hideModal(refName) {
        this.$refs[refName].hide();
      },

      searchGoodss() {
        this.$refs.vuetable.refresh();
      },
      resetGoodsSearchForm() {
        this.goods = '';
      },
      onClickCreateGoods() {
        this.showable = false;
        this.selectedGoods = null;
        this.goodsForm = {
          visible : true,
          goodsName: '',
          goodsGrade: '',
          goodsCategory: ''
        }
      },
      onClickSaveGoods() {

          if(this.$v.goodsForm.$invalid){
            if(this.goodsForm.goodsName===""){
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.goods-invalid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            if(this.goodsForm.goodsGrade===""){
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.goods-grade-invalid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            if(this.goodsForm.goodsCategory===""){
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.goods-category-invalid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            return;
          }

          this.isLoading = true;
          if(this.selectedGoods) {
            getApiManager()
              .post(`${apiBaseUrl}/seized-good-management/seized/modify`, {
                'goodsId': this.selectedGoods.goodsId,
                'seizedGoodsCode': this.goodsForm.goodsName,
                'seizedGoodType': this.goodsForm.goodsCategory,
                'seizedGoodsLevel': this.goodsForm.goodsGrade,
              })
              .then((response) => {
                this.isLoading = false;
                let message = response.data.message;
                let data = response.data.data;
                switch (message) {
                  case responseMessages['ok']:
                    this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.seized-modify`), {
                      duration: 3000,
                      permanent: false
                    });
                    this.$refs.vuetable.reload();
                    break;
                  case responseMessages['used-seized-good']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-seized-good`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
                  case responseMessages['invalid-parameter']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
                  default:

                }
              })
              .catch((error) => {
                this.isLoading = false;
              });
          }
          if(this.goodsForm.visible){
            getApiManager()
              .post(`${apiBaseUrl}/seized-good-management/seized/create`, {
                'seizedGoodsCode': this.goodsForm.goodsName,
                'seizedGoodType': this.goodsForm.goodsCategory,
                'seizedGoodsLevel': this.goodsForm.goodsGrade,
              })
              .then((response) => {
                this.isLoading = false;
                let message = response.data.message;
                let data = response.data.data;
                switch (message) {
                  case responseMessages['ok']:
                    this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.seized-create`), {
                      duration: 3000,
                      permanent: false
                    });
                    this.$refs.vuetable.reload();
                    break;
                  case responseMessages['used-seized-good']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-seized-good`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
                  case responseMessages['invalid-parameter']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
                  default:

                }
              })
              .catch((error) => {
                this.isLoading = false;
              });
          }

      },
      onClickDeleteGoods(goods) {
        this.deletingGoods = goods;
        this.$refs['modal-delete-goods'].show();
      },
      deleteGoods() {
        if (this.deletingGoods) {
          getApiManager()
            .post(`${apiBaseUrl}/seized-good-management/seized/delete`, {
              'goodsId': this.deletingGoods.goodsId
            })
            .then((response) => {
              this.isLoading = false;
              this.hideModal('modal-delete-goods')
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.seized-delete`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.deletingGoods = null;
                  this.$refs.vuetable.refresh();
                  break;
                case responseMessages['used-seized-good']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-seized-good`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['invalid-parameter']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-message.invalid-parameter`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
      },

      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            goodsCode: this.goods,
          }
        });
      },
      onGoodsPaginationData(paginationData) {
        this.selectedGoods =false;
        this.goodsForm.visible =false;
        this.$refs.goodsPagination.setPaginationData(paginationData)
      },
      onGoodsNumberClicked(dataItem, show=false) {
        this.showable = show;
        this.goodsForm.visible = false;
        this.selectedGoods = JSON.parse(JSON.stringify(dataItem));
        this.goodsForm.goodsName = this.selectedGoods.seizedGoodsCode;
        this.goodsForm.goodsCategory = this.selectedGoods.seizedGoodType;
        this.goodsForm.goodsGrade = this.selectedGoods.seizedGoodsLevel;
      },
      onGoodsChangePage(page) {
        this.selectedGoods =false;
        this.goodsForm.visible =false;
        this.$refs.vuetable.changePage(page);
      }
    }
  }
</script>
