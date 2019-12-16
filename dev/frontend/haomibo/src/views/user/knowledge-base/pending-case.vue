<style lang="scss">
  .rounded-span {
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }

  .operation-icon {
    width: 24px;
    height: 24px;
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
                  <b-form-select v-model="filter.modeName" :options="modeOption" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.task-result')">
                  <b-form-select v-model="filter.taskResult" :options="handResultOption" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('knowledge-base.site')">
                  <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
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
                track-by="caseDealId"
                :api-url="pendingListTableItems.apiUrl"
                :fields="pendingListTableItems.fields"
                :http-fetch="pendingListTableHttpFetch"
                :per-page="pendingListTableItems.perPage"
                @vuetable:checkbox-toggled-all="onCheckEvent"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <template slot="task" slot-scope="props">
                    <span class="cursor-p text-primary" v-if="props.rowData.task!=null">
                      {{props.rowData.task.taskNumber}}
                    </span>
                  <span v-else> </span>
                </template>
                <template slot="scanImage" slot-scope="props">
                  <b-img v-if="props.rowData.scanImageUrl != null" :src="props.rowData.scanImageUrl"
                         class="operation-icon"/>
                  <b-img v-else/>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="props.rowData.workMode==null"></div>
                  <div v-else>
                    <div v-if="props.rowData.workMode.modeName==='1000001304'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName==='1000001301'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName==='1000001302'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName==='1000001303'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                  </div>
                </template>
                <template slot="handTaskResult" slot-scope="props">
                  <div>{{getDictDataValue(props.rowData.handTaskResult, 6)}}</div>
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
  import {getDictData, checkBoxListDic, checkBoxListDeviceDic} from '../../../utils'

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted() {
      this.getModeOption();
      this.gethandResultOption();
      this.getSiteOption();

    },
    data() {
      return {
        isExpanded: false,
        isCheckAll: false,
        idList: [],
        filter: {
          fieldId: null,
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
        modeData: [],
        handResultData: [],
        modeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001304', text: '安检仪+审图端+手检端'},
          {value: '1000001301', text: '安检仪+(本地手检)'},
          {value: '1000001302', text: '安检仪+手检端'},
          {value: '1000001303', text: '安检仪+审图端'},
        ],
        siteOptions: [
          {value: 'male', text: this.$t('knowledge-base.all')},
          {value: 'female', text: this.$t('knowledge-base.airport')},
          {value: 'unknown', text: this.$t('knowledge-base.port')},
          {value: 'unknown', text: this.$t('knowledge-base.land-border')},
        ],
        resultTypeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'doubt', text: this.$t('knowledge-base.suspect')},
          {value: 'nodoubt', text: this.$t('knowledge-base.no-suspect')},
          {value: 'seized', text: this.$t('knowledge-base.seized')},
          {value: 'noseizure', text: this.$t('knowledge-base.no-seized')},
        ],
        modeOption: [],
        handResultOption: [],
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
              name: '__slot:handTaskResult',
              title: this.$t('knowledge-base.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanDevice) => {
                if (scanDevice == null) return '';
                if (scanDevice.field == null) return '';
                return scanDevice.field.fieldDesignation;
              }
            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanDevice) => {
                if (scanDevice == null) return '';
                return scanDevice.deviceName;
              }
            },
            {
              name: 'judgeDevice',
              title: this.$t('knowledge-base.inspection-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeDevice) => {
                if (judgeDevice == null) return '';
                return judgeDevice.deviceName;
              }
            },
            {
              name: 'handDevice',
              title: this.$t('knowledge-base.hand-inspection-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handDevice) => {
                if (handDevice == null) return '';
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
      },

      modeData: function (newVal, oldVal) {
        //console.log(newVal);
        this.modeOption = [];
        this.modeOption = newVal.map(mode => ({
          text: mode.dataValue,
          value: mode.dataCode
        }));
        this.modeOption.push({
          text: this.$t('personal-inspection.all'),
          value: null
        });
        if (this.modeOption.length === 0)
          this.modeOption.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      },

      handResultData: function (newVal, oldVal) {
        //console.log(newVal);
        this.handResultOption = [];
        this.handResultOption = newVal.map(mode => ({
          text: mode.dataValue,
          value: mode.dataCode
        }));
        this.handResultOption.push({
          text: this.$t('personal-inspection.all'),
          value: null
        });
        if (this.handResultOption.length === 0)
          this.handResultOption.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      }
    },
    methods: {
      onCheckEvent() {
        //this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}})
        let isCheck = this.isCheckAll;
        let cnt = this.$refs.pendingListTable.selectedTo.length;

        if (cnt === 0) {
          this.isCheckAll = false;
        } else {
          this.isCheckAll = true;
        }
        console.log(this.isCheckAll);

        // console.log(isCheck);
        // if (isCheck === false) {
        //   console.log(this.idList.length);
        //   // for (let i =0; i<this.idList.length; i++){
        //   //   this.$refs.pendingListTable.selectedTo.push(this.idList[i]);
        //   // }
        //   this.$refs.pendingListTable.selectedTo.push(12);
        //   console.log(this.$refs.pendingListTable.selectedTo);
        //   this.isCheckAll = true;
        // }
        // else {
        //   this.$refs.pendingListTable.selectedTo = [];
        //   this.isCheckAll = false;
        // }
      },
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
      onResetButton() {
        this.filter = {
          taskNumber: null,
          modeName: null,
          taskResult: null,
          fieldId: null,
          fieldDesignation: null,
          handGoods: null,
        };
        //this.$refs.pendingListTable.refresh();
      },

      onGenerateExcelButton() {
        let str = "";
        if (this.isCheckAll === true) {
          str = "";
        } else {
          let cnt = this.$refs.pendingListTable.selectedTo.length;
          str = str + this.$refs.pendingListTable.selectedTo[0];
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {
            //console.log(this.$refs.taskVuetable.selectedTo[i]);
            str = str + "," + this.$refs.pendingListTable.selectedTo[i];
            //console.log(str);
          }
        }
        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/generate/pending/export`, {
            'isAll': this.isCheckAll,
            'filter': {"caseStatus": 'submit_approval'},
            'exportType': 'excel',
            'idList': str
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

        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/generate/pending/word`, {
            'isAll': this.isCheckAll,
            'filter': {"caseStatus": 'submit_approval'},
            'exportType': 'word',
            'idList': str
          }, {
            responseType: 'blob'
          })
          .then((response) => {
            let fileURL = window.URL.createObjectURL(new Blob([response.data]));
            let fileLink = document.createElement('a');

            fileLink.href = fileURL;
            fileLink.setAttribute('download', 'knowledge-pending.docx');
            document.body.appendChild(fileLink);

            fileLink.click();
          })
          .catch(error => {
            throw new Error(error);
          });
      },

      onGeneratePdfButton() {
        let str = "";

        if (this.isCheckAll === true) {
          str = "";
        } else {
          let cnt = this.$refs.pendingListTable.selectedTo.length;
          str = str + this.$refs.pendingListTable.selectedTo[0];
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {
            //console.log(this.$refs.taskVuetable.selectedTo[i]);
            str = str + "," + this.$refs.pendingListTable.selectedTo[i];
            //console.log(str);
          }
        }
        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/generate/pending/print`, {
            'isAll': this.isCheckAll,
            'filter': {"caseStatus": 'submit_approval'},
            'exportType': 'pdf',
            'idList': str
          }, {
            responseType: 'blob'
          })
          .then((response) => {
            let fileURL = window.URL.createObjectURL(new Blob([response.data], {type: "application/pdf"}));
            var objFra = document.createElement('iframe');   // Create an IFrame.
            objFra.style.visibility = "hidden";    // Hide the frame.
            objFra.src = fileURL;                      // Set source.
            document.body.appendChild(objFra);  // Add the frame to the web page.
            objFra.contentWindow.focus();       // Set focus.
            objFra.contentWindow.print();
          })
          .catch(error => {
            throw new Error(error);
          });


      },



      getModeOption() {
        let data = checkBoxListDic(13);
        this.modeData = data;
        //console.log(this.modeData);
      },

      gethandResultOption() {
        let data = checkBoxListDeviceDic(6);
        this.handResultData = data;
      },

      getDictDataValue(dataCode, dicId = null) {
        return getDictData(dataCode, dicId);
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
          if (temp.scanImage != null) {
            temp.scanImageUrl = apiBaseUrl + temp.scanImage.imageUrl;
          }
          transformed.data.push(temp);
          this.idList.push(idTemp);
          if (this.isCheckAll === true) {
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
            caseStatus: this.filter.caseStatus,
            taskNumber: this.filter.taskNumber,
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

      onAction(action, data) { // called when any action button is called from table

        let successItem = () => {

          this.actionFilter = {
            caseId: data,
            status: "success_approval",
          };
        };

        let dismissItem = () => {
          this.actionFilter = {
            caseId: data,
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


