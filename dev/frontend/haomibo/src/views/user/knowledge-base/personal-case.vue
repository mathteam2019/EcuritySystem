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
                  <b-img v-if="props.rowData.scanImageUrl != null" :src="props.rowData.scanImageUrl" class="operation-icon" />
                  <b-img v-else/>
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
      //this.$refs.taskVuetable.$parent.transform = this.transform.bind(this);
      this.getSiteOption();

    },

    data() {
      return {
        pageStatus: 'list',
        isExpanded:false,
        isCheckAll:false,
        idList:[],
        filter: {
          fieldId : null,
              caseStatus:"success_approval",
              taskNumber: null,
              modeName: null,
              taskResult: null,
              fieldDesignation:null,
              handGoods:null,
          },

        siteData: [],
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
              name: 'handTaskResult',
              title: this.$t('knowledge-base.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handTaskResult) => {

                const dictionary = {
                  "noseizure": `<span style="color:#e8a23e;">无查获</span>`,
                  "seized": `<span style="color:#e8a23e;">有查获</span>`,
                  "doubt": `<span style="color:#ef6e69;">有嫌疑</span>`,
                  "nodoubt": `<span style="color:#e8a23e;">无嫌疑</span>`,
                  "while_inspection": `<span style="color:#ef6e69;">${this.$t('personal-inspection.while-inspection')}</span>`,
                };

                if(handTaskResult==null) return '';
                if (!dictionary.hasOwnProperty(handTaskResult)) return 'Invalid';
                return dictionary[handTaskResult];
              }
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
      onCheckEvent(){
        //this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}})
        let isCheck = this.isCheckAll;
        let cnt = this.$refs.pendingListTable.selectedTo.length;
        console.log(cnt);
        if(cnt === 0){
          this.isCheckAll = false;
        }
        else {
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

        //this.$refs.pendingListTable.selectedTo.push(12);
      },
      onResetButton(){
        this.filter = {
            taskNumber: null,
            modeName: null,
            taskResult: null,
	    fieldId:null,
            fieldDesignation:null,
            handGoods:null,
        };
        //this.$refs.pendingListTable.refresh();
      },

      onGenerateExcelButton(){
        let str = "";
        if (this.isCheckAll === true) {
          str = "";
        }
        else {
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
          .post(`${apiBaseUrl}/knowledge-base/generate/personal/export`, {
            'isAll' : this.isCheckAll,
            'filter' : {"caseStatus": 'success_approval'},
            'exportType' : 'excel',
            'idList' : str
          }, {
            responseType: 'blob'
          })
          .then((response) => {
            let fileURL = window.URL.createObjectURL(new Blob([response.data]));
            let fileLink = document.createElement('a');

            fileLink.href = fileURL;
            fileLink.setAttribute('download', 'knowledge-personal.xlsx');
            document.body.appendChild(fileLink);

            fileLink.click();
          })
          .catch(error => {
            throw new Error(error);
          });
      },

      onGeneratePdfButton(){
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
          .post(`${apiBaseUrl}/knowledge-base/generate/personal/print`, {
            'isAll' : this.isCheckAll,
            'filter' : {"caseStatus": 'success_approval'},
            'exportType' : 'pdf',
            'idList' : str
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
                temp.scanImageUrl = apiBaseUrl+ temp.scanImage.imageUrl;
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


