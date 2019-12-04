<style lang="scss">
  .rounded-span {
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }
</style>
<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('knowledge-base.task-number')">
                  <b-form-input v-model="filter.taskNumber"></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.operating-mode')">
                  <b-form-select v-model="filter.modeName"  :options="modeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.task-result')">
                  <b-form-select v-model="filter.taskResult"  :options="resultTypeOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('knowledge-base.site')">
                  <b-form-select v-model="filter.fieldDesignation"  :options="siteOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="8" v-if="isExpanded">
            <b-row>
              <b-col>
                <b-form-group :label="$t('knowledge-base.seized-item')">
                  <b-form-input v-model="filter.handGoods"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col></b-col>
              <b-col></b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" @click="onGenerateExcelButton()">
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default"  @click="onGeneratePdfButton()">
                <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="pendingListTable"
                track-by="caseDealId"
                :api-url="pendingListTableItems.apiUrl"
                :fields="pendingListTableItems.fields"
                :http-fetch="pendingListTableHttpFetch"
                :per-page="pendingListTableItems.perPage"
                @vuetable:checkbox-toggled-all = "onCheckEvent"
                @vuetable:checkbox-toggled = "onCheckOneEvent"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <div slot="task" slot-scope="props">
                  <span class="cursor-p text-primary">{{ props.rowData.task.taskNumber}}</span>
                </div>
                <template slot="scanImage" slot-scope="props">
                  <b-img :src="props.rowData.scanImage.imageUrl" class="operation-icon" />
                </template>
                <div slot="operating" slot-scope="props">
                  <b-button
                    size="sm"
                    variant="danger default btn-square"
                    @click="onAction(props.rowData.caseId)">
                    <i class="icofont-ban"></i>
                  </b-button>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pendingListTablePagination"
                :initial-per-page="pendingListTableItems.perPage"
                @vuetable-pagination:change-page="onBlackListTableChangePage"
                @onUpdatePerPage="pendingListTableItems.perPage = Number($event)"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

  </div>
</template>
<script>
    import {apiBaseUrl} from "../../../constants/config";
    import Vuetable from '../../../components/Vuetable2/Vuetable'
    import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';
    import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
    import Switches from 'vue-switches';


    export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },

    mounted() {

    },

    data() {
      return {
        pageStatus: 'list',
        isExpanded:false,
        isCheckAll:false,
        idList:[],
        filter: {
              caseStatus:"success_approval",
              taskNumber: null,
              modeName: null,
              taskResult: null,
              fieldDesignation:null,
              handGoods:null,
          },

        modeOptions:[
          {value: '1', text: this.$t('knowledge-base.security-instrument')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test-and-device')},
        ],
        siteOptions: [
          {value: 'male', text: this.$t('knowledge-base.all')},
          {value: 'female', text: this.$t('knowledge-base.airport')},
          {value: 'unknown', text: this.$t('knowledge-base.port')},
          {value: 'unknown', text: this.$t('knowledge-base.land-border')},
        ],
        resultTypeOptions: [
          {value: 'male', text: this.$t('knowledge-base.no-suspect')},
          {value: 'female', text: this.$t('knowledge-base.seized')},
          {value: 'unknown', text: this.$t('knowledge-base.no-seized')},
        ],
        pendingListTableItems: {
          apiUrl: `${apiBaseUrl}/knowledge-base/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'caseDealId',
              sortField: 'caseDealId',
              title: this.$t('knowledge-base.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:task',
              title: this.$t('knowledge-base.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:scanImage',
              title: this.$t('knowledge-base.image'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },

            {
              name: 'handResult',
              title: this.$t('knowledge-base.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
                callback: (scanDevice) => {
                    if(scanDevice==null)  return '';
                    if(scanDevice.field==null)  return '';
                    return scanDevice.field.fieldDesignation;
                }
            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.channel'),
              titleClass: 'text-center',
              dataClass: 'text-center',
                callback: (scanDevice) => {
                    if(scanDevice==null)  return '';
                    return scanDevice.devicePassageWay;
                }
            },

            {
              name: 'handGoods',
              title: this.$t('knowledge-base.seized-item'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '150px'
            }
          ]
        },
      }
    },
    methods: {

      onCheckOneEvent(){
        

      },
      onCheckEvent(){
        
      },
      onSearchButton(){
        this.$refs.pendingListTable.refresh();

        this.$refs.pendingListTable.selectedTo.push(12);
      },
      onResetButton(){
        this.filter = {
            taskNumber: null,
            modeName: null,
            taskResult: null,
            fieldDesignation:null,
            handGoods:null,
        };
        
      },

      onGenerateExcelButton(){
        
      },

      onGeneratePdfButton(){
        

      },

        transform(response) {

            let transformed = {};

            let data = response.data;

            transformed.pagination = {
                total: data.total,
                per_page: data.per_page,
                current_page: data.current_page,
                last_page: data.last_page,
                from: data.from,
                to: data.to
            };

            transformed.data = [];
            //transformed.idList = [];
            let temp;
            let idTemp;
            for (let i = 0; i < data.data.length; i++) {
                temp = data.data[i];
                idTemp = data.data[i].caseDealId;
                transformed.data.push(temp);
                this.idList.push(idTemp);
                if(this.isCheckAll === true){
                  this.$refs.pendingListTable.selectedTo.push(idTemp);
                }
            }

            return transformed

        },

      pendingListTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.pendingListTableItems.perPage,

          filter: {
              caseStatus:this.filter.caseStatus,
              taskNumber:this.filter.taskNumber,
              modeName: this.filter.modeName,
              taskResult: this.filter.taskResult,
              fieldDesignation: this.filter.fieldDesignation,
              handGoods: this.filter.handGoods,
          },
        });
      },
      onBlackListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
      },
      onBlackListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
      },

      onAction(data) { // called when any action button is called from table
                // call api
                getApiManager()
                    .post(`${apiBaseUrl}/knowledge-base/update-status`, {
                        'caseId': data,
                        'status': "submit_approval",
                    })
                    .then((response) => {
                        let message = response.data.message;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-activated-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                this.$refs.pendingListTable.refresh();
                                break;

                        }
                    })
                    .catch((error) => {
                    });







        },
    }
  }
</script>


