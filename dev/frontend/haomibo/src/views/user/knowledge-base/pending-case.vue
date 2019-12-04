<style lang="scss">
  .rounded-span{
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
          <piaf-breadcrumb />
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
                  <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
                </b-form-group>
              </b-col>
              <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded" >
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
              <b-button size="sm" class="ml-2" variant="outline-info default" @click="onGeneratePdfButton()">
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
                track-by = "caseDealId"
                :api-url="pendingListTableItems.apiUrl"
                :fields="pendingListTableItems.fields"
                :http-fetch="pendingListTableHttpFetch"
                :per-page="pendingListTableItems.perPage"
                @vuetable:checkbox-toggled="toggledChbox(payload, dataItem)"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <template slot="task" slot-scope="props">
                    <span class="cursor-p text-primary">
                      {{ props.rowData.task.taskNumber}}
                    </span>
                </template>
                <template slot="scanImage" slot-scope="props">
                  <b-img :src="props.rowData.scanImage.imageUrl" class="operation-icon" />
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="filter.modeName==null">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon" />
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon" />
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon" />
                  </div>
                  <div v-if="filter.modeName==='security'">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon" />
                  </div>
                  <div v-if="filter.modeName==='security+hand'">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon" />
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon" />
                  </div>
                </template>
                <template slot="operating" slot-scope="props">
                  <div>
                  <b-button
                    size="sm"
                    variant="success default btn-square"
                    @click="onAction('success', props.rowData.caseId)">
                    <i class="icofont-check-alt"></i>
                  </b-button>
                  <b-button
                    size="sm"
                    variant="danger default btn-square"
                    @click="onAction('dismiss', props.rowData.caseId)">
                    <i class="icofont-ban"></i>
                  </b-button>
                  </div>
                </template>
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
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {getApiManager} from "../../../api";
  import {apiBaseUrl} from "../../../constants/config";
  import {responseMessages} from '../../../constants/response-messages';

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted() {
      //this.$refs.taskVuetable.$parent.transform = this.transform.bind(this);
      this.getSiteOption();

    },
    data() {
      return {
        isExpanded:false,
        filter: {
          fieldId : null,
          caseStatus: 'submit_approval',
          taskNumber: null,
          modeName: null,
          taskResult: null,
          fieldDesignation: null,
          handGoods: null,
        },
        actionFilter: {
          caseId: null,
          status: null,
        },
        siteData: [],
        modeOptions: [
          {value: '1', text: this.$t('knowledge-base.security-instrument')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test')},
          {value: '2', text: this.$t('knowledge-base.security-instrument-and-hand-test-and-device')},
        ],
        siteOptions:[
          {value: 'male', text: this.$t('knowledge-base.all')},
          {value: 'female', text: this.$t('knowledge-base.airport')},
          {value: 'unknown', text: this.$t('knowledge-base.port')},
          {value: 'unknown', text: this.$t('knowledge-base.land-border')},
        ],
        resultTypeOptions:[
          {value: 'male', text: this.$t('knowledge-base.no-suspect')},
          {value: 'female', text: this.$t('knowledge-base.seized')},
          {value: 'unknown', text: this.$t('knowledge-base.no-seized')},
        ],
        onSiteOption: [],
        pendingListTableItems: {
          apiUrl: `${apiBaseUrl}/knowledge-base/get-by-filter-and-page`,
          perPage: 10,
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
              name: '__slot:mode',
              title: this.$t('knowledge-base.operating-mode'),
              titleClass: 'text-center',
              dataClass: 'text-center',
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
                    return scanDevice.field.fieldDesignation;
                }
            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center',
                callback: (scanDevice) => {
                    if(scanDevice==null)  return '';
                    return scanDevice.deviceName;
                }
            },
            {
              name: 'judgeDevice',
              title: this.$t('knowledge-base.inspection-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
                callback: (judgeDevice) => {
                    if(judgeDevice==null)  return '';
                    return judgeDevice.deviceName;
                }
            },
            {
              name: 'handDevice',
              title: this.$t('knowledge-base.hand-inspection-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
                callback: (handDevice) => {
                    if(handDevice==null)  return '';
                    return handDevice.deviceName;
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
    watch: {
      'pendingListTableItems.perPage': function (newVal) {
        this.$refs.pendingListTable.refresh();
      },
      siteData: function (newVal, oldVal) {
        console.log(newVal);
        this.onSiteOption = [];
        this.onSiteOption = newVal.map(site => ({
          text: site.fieldDesignation,
          value: site.fieldId
        }));
        this.onSiteOption.push({
          text: this.$t('personal-inspection.all'),
          value: null
        });
        if (this.onSiteOption.length === 0)
          this.onSiteOption.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      }
    },
    methods: {
      getSiteOption() {
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        })
          .catch((error) => {
          });

      },
      onSearchButton() {
        this.$refs.pendingListTable.refresh();
      },
      onGenerateExcelButton(){
          getApiManager()
              .post(`${apiBaseUrl}/knowledge-base/generate/pending/export`, {
                  'isAll' : true,
                  'idList' : '',
                  'exportType' : 'excel',
              }, {
                  responseType: 'blob'
              })
              .then((response) => {
                  let fileURL = window.URL.createObjectURL(new Blob([response.data]));
                  let fileLink = document.createElement('a');

                  fileLink.href = fileURL;
                  fileLink.setAttribute('download', 'knowledge-pending.xlsx');
                  document.body.appendChild(fileLink);

                  fileLink.click();
              })
              .catch(error => {
                  throw new Error(error);
              });
      },

        onGeneratePdfButton(){
            getApiManager()
                .post(`${apiBaseUrl}/knowledge-base/generate/pending/export`, {
                    'isAll' : true,
                    'idList' : '',
                    'exportType' : 'pdf',
                }, {
                    responseType: 'blob'
                })
                .then((response) => {
                    let fileURL = window.URL.createObjectURL(new Blob([response.data]));
                    let fileLink = document.createElement('a');

                    fileLink.href = fileURL;
                    fileLink.setAttribute('download', 'knowledge-pending.pdf');
                    document.body.appendChild(fileLink);

                    fileLink.click();
                })
                .catch(error => {
                    throw new Error(error);
                });


        },
      onResetButton(){
        this.filter = {
            taskNumber: null,
            modeName: null,
            taskResult: null,
            fieldDesignation:null,
          fieldId : null,
            handGoods:null,
        };
        
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
            let temp;
            for (let i = 0; i < data.data.length; i++) {
                temp = data.data[i];
                transformed.data.push(temp)
            }

            return transformed

        },

      pendingListTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

          console.log(this.filter.taskNumber);
          console.log(this.filter.modeName);
          console.log(this.filter.taskResult);
          console.log(this.filter.fieldDesignation);
          console.log(this.filter.handGoods);

          return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,

          filter: {
              caseStatus:this.filter.caseStatus,
              taskNumber:this.filter.taskNumber,
              modeName: this.filter.modeName,
              taskResult: this.filter.taskResult,
              fieldDesignation: this.filter.fieldDesignation,
              handGoods: this.filter.handGoods,
          },
          perPage: this.pendingListTableItems.perPage,
        });
      },
      onBlackListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
      },
      onBlackListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
      },

      onAction(action, data) { // called when any action button is called from table

            let successItem = () => {

                this.actionFilter = {
                    caseId:data,
                    status: "success_approval",
                };
            };

            let dismissItem = () => {
                this.actionFilter = {
                    caseId:data,
                    status: "dismiss",
                };
            };

            let activateItem = () => {
                // call api
                getApiManager()
                    .post(`${apiBaseUrl}/knowledge-base/update-status`, {
                        'caseId': this.actionFilter.caseId,
                        'status': this.actionFilter.status,
                    })
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
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


            };



            switch (action) {
                case 'success':
                    successItem();
                    activateItem();
                    break;
                case 'dismiss':
                    dismissItem();
                    activateItem();
                    break;
            }
        },
    }
  }
</script>


