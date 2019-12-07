<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-card class="main-without-tab" v-if="pageStatus === 'table'">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input v-model="filter.taskNumber"></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.operation-mode')">
                  <b-form-select v-model="filter.mode" :options="operationModeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.status')">
                  <b-form-select v-model="filter.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.on-site')">
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
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input v-model="filter.userName"></b-form-input>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.start-time')">
                  <date-picker v-model="filter.startTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""></date-picker>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.end-time')">
                  <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""></date-picker>
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
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export')}}
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
                ref="taskVuetable"
                track-by="taskId"
                :api-url="taskVuetableItems.apiUrl"
                :fields="taskVuetableItems.fields"
                :http-fetch="taskVuetableHttpFetch"
                :per-page="taskVuetableItems.perPage"
                @vuetable:checkbox-toggled-all="onCheckEvent"
                pagination-path="pagination"
                class="table-hover"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="task" slot-scope="props">
                    <span v-if="props.rowData.task!=null" class="cursor-p text-primary"
                          @click="onRowClicked(props.rowData.historyId)">
                      {{props.rowData.task.taskNumber}}
                    </span>
                  <span v-else> </span>
                </template>
                <template slot="scanImage" slot-scope="props">
                  <b-img v-if="props.rowData.scanImage != null" :src="props.rowData.scanImage.imageUrl"
                         class="operation-icon"/>
                  <b-img v-else/>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="props.rowData.workMode==null">None</div>
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
                <template slot="taskStatus" slot-scope="props">
                  <div v-if="props.rowData.taskStatus === 'pending_dispatch'" style="color:#e8a23e;">
                    {{$t('personal-inspection.pending-dispatch')}}
                  </div>
                  <div v-if="props.rowData.taskStatus === 'pending_review'" style="color:#e8a23e;">
                    {{$t('personal-inspection.pending-review')}}
                  </div>
                  <div v-if="props.rowData.taskStatus === 'while_review'" style="color:#ef6e69;">
                    {{$t('personal-inspection.while-review')}}
                  </div>
                  <div v-if="props.rowData.taskStatus === 'pending_inspection'" style="color:#e8a23e;">
                    {{$t('personal-inspection.pending-inspection')}}
                  </div>
                  <div v-if="props.rowData.taskStatus === 'while_inspection'" style="color:#ef6e69;">
                    {{$t('personal-inspection.while-inspection')}}
                  </div>
                </template>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="taskVuetablePagination"
                @vuetable-pagination:change-page="onTaskVuetableChangePage"
                :initial-per-page="taskVuetableItems.perPage"
                @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <div v-if="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col cols="3">
          <b-card class="pt-4 h-100">
            <b-row class="mb-1">
              <b-col>
                <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                <b-img src="/assets/img/monitors_icon.svg" class="operation-icon ml-2"/>
                <b-img src="/assets/img/mobile_icon.svg" class="operation-icon ml-2"/>
              </b-col>
              <b-col class="text-right icon-container">
                <span><i class="icofont-star"></i></span>
                <span><i class="icofont-search-user"></i></span>
                <span><i class="icofont-female"></i></span>
              </b-col>
            </b-row>

            <b-row class="mb-4">
              <b-col>
                <b-img src="/assets/img/scan-rl.gif" fluid-grow></b-img>
              </b-col>
              <b-col>
                <b-img src="/assets/img/scan-lr.gif" fluid-grow></b-img>
              </b-col>
            </b-row>

            <b-row class="mb-2">
              <b-col class="control-group">
                <div class="control-btn-wrapper">

                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>


                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>

                <div class="switch-wrapper">
                  <div class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" theme="custom" color="info"></switches>
                  </div>
                </div>
              </b-col>
            </b-row>


          </b-card>
        </b-col>
        <b-col cols="9">
          <b-card class="h-100 d-flex flex-column right-card">
            <div class="history-chart">
              <div>

                <div class="part">
                  <div class="left">
                    <div>开始</div>
                  </div>
                  <div class="right">
                    <div>Start</div>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>扫描</div>
                    <div>
                      <div v-if="showPage.scanPointsmanName != null">{{showPage.scanPointsmanName}}</div>
                      <div v-else>None</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Scanning</div>
                    <div>zhang san</div>
                  </div>
                  <div class="top-date">
                    <label
                      v-if="showPage.scanStartTime != null">{{this.getDateTimeFormat2(showPage.scanStartTime)}}</label>
                    <label v-else>None</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.scanEndTime != null">{{this.getDateTimeFormat2(showPage.scanEndTime)}}</label>
                    <label v-else>None</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>判图</div>
                    <div>
                      <div v-if="showPage.judgeUser != null">{{showPage.judgeUser.userName}}</div>
                      <div v-else>None</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Decision diagram</div>
                    <div>Li si</div>
                  </div>
                  <div class="top-date">
                    <label
                      v-if="showPage.judgeStartTime != null">{{this.getDateTimeFormat2(showPage.judgeStartTime)}}</label>
                    <label v-else>None</label>
                  </div>
                  <div class="bottom-date">
                    <label
                      v-if="showPage.judgeEndTime != null">{{this.getDateTimeFormat2(showPage.judgeEndTime)}}</label>
                    <label v-else>None</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>查验</div>
                    <div>
                      <div v-if="showPage.handUser == null">None</div>
                      <div v-else>{{showPage.handUser.userName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Inspection</div>
                    <div>Wang wu</div>
                  </div>
                  <div class="top-date">
                    <label v-if="showPage.handStartTime == null">None</label>
                    <label v-else>{{this.getDateTimeFormat2(showPage.handStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.handEndTime == null">None</label>
                    <label v-else>{{this.getDateTimeFormat2(showPage.handEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>结束</div>
                  </div>
                  <div class="right">
                    <div>End</div>
                  </div>
                </div>

              </div>

            </div>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.task == null">None</label>
                  <label v-else>{{showPage.task.taskNumber}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.task == null">None</label>
                  <label v-else-if="showPage.task.field == null">None</label>
                  <label v-else>{{showPage.task.field.fieldDesignation}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.scanDevice == null">None</label>
                  <label v-else>{{showPage.scanDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.task.serScan == null">None</label>
                  <label v-else-if="showPage.task.serScan.scanImageGender === 'male'">男</label>
                  <label v-else-if="showPage.task.serScan.scanImageGender === 'female'">女</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scanned-image')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-img v-if="showPage.scanImage != null" :src="showPage.scanImage.imageUrl" class="operation-icon"/>
                  <!--                  <label v-if="showPage.serScan != null">{{showPage.scanImage.imageUrl}}</label>-->
                  <!--                  <label v-else>None</label>-->
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <div v-if="showPage.workMode==null">None</div>
                  <div v-else>
                    <div v-if="showPage.workMode.modeName==='1000001304'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="showPage.workMode.modeName==='1000001301'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="showPage.workMode.modeName==='1000001302'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="showPage.workMode.modeName==='1000001303'">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.guide')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.scanPointsmanName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.atr-conclusion')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.scanAtrResult == null">None</label>
                  <label v-else-if="showPage.scanAtrResult==='true'">无嫌疑</label>
                  <label v-else-if="showPage.scanAtrResult==='false'">嫌疑</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.foot-alarm')}}
                  </template>
                  <label v-if="showPage.scanFootAlarm == null">None</label>
                  <label v-else-if="showPage.scanFootAlarm==='true'">无</label>
                  <label v-else-if="showPage.scanFootAlarm==='false'">有</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serScan != null">{{this.getDateTimeFormat(showPage.scanStartTime)}}</label>
                  <label v-else>None</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.scan-end-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serScan != null">{{this.getDateTimeFormat(showPage.scanEndTime)}}</label>
                  <label v-else>None</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.dispatch-timeout')}}
                  </template>
                  <label v-if="showPage.assignTimeout == null">None</label>
                  <label v-else-if="showPage.assignTimeout==='true'">无</label>
                  <label v-else-if="showPage.assignTimeout==='false'">有</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.judgeDevice == null">None</label>
                  <label v-else>{{showPage.judgeDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.judgeResult}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.judgeResult == null">None</label>
                  <label v-else-if="showPage.judgeResult==='true'">无嫌疑</label>
                  <label v-else-if="showPage.judgeResult==='false'">嫌疑</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>

              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-timeout')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.judgeTimeout == null">None</label>
                  <label v-else-if="showPage.judgeTimeout==='true'">无</label>
                  <label v-else-if="showPage.judgeTimeout==='false'">有</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label
                    v-if="showPage.judgeStartTime != null">{{this.getDateTimeFormat(showPage.judgeStartTime)}}</label>
                  <label v-else>None</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-end-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.judgeEndTime != null">{{this.getDateTimeFormat(showPage.judgeEndTime)}}</label>
                  <label v-else>None</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judge')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.judgeUser == null">None</label>
                  <label v-else>{{showPage.judgeUser.userName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.handDevice == null">None</label>
                  <label v-else>{{showPage.handDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>

              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-start-time')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.handStartTime == null">None</label>
                  <label v-else>{{this.getDateTimeFormat(showPage.handStartTime)}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-checker')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.handUser == null">None</label>
                  <label v-else>{{showPage.handUser.userName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-result')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.handTaskResult == null">None</label>
                  <label v-else-if="showPage.handTaskResult==='noseizure'" style="color:#e8a23e;">无查获</label>
                  <label v-else-if="showPage.handTaskResult==='seized'" style="color:#e8a23e;">有查获</label>
                  <label v-else-if="showPage.handTaskResult==='doubt'" style="color:#ef6e69;">有嫌疑</label>
                  <label v-else-if="showPage.handTaskResult==='nodoubt'" style="color:#e8a23e;">无嫌疑</label>
                  <label v-else>Invalid Value</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.item-level')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.handGoodsGrade}} </label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.handAppraise}} </label>
                </b-form-group>
              </b-col>

            </b-row>

            <b-row>
              <b-col></b-col>
              <b-col></b-col>
              <b-col></b-col>
            </b-row>

            <b-row class="flex-grow-1 d-flex align-items-end">
              <b-col>
                <label class="font-weight-bold">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`"
                         @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 75px; height: 60px;" :alt="thumb.name"/>
                    <label class="d-block text-center mt-2">{{thumb.name}}</label>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()"/>
                </b-row>
              </b-col>

              <b-col class="seized-list">
                <label class="font-weight-bold">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="d-flex justify-content-end">
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff0000">
                      <b-img src="/assets/img/pistol_icon.svg" class="contraband-icon"/>
                      <span class="contraband-count">2</span>
                      <span class="contraband-category">{{$t('personal-inspection.firearms')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff4e00">
                      <b-img src="/assets/img/drug_icon.svg" class="contraband-icon"/>
                      <span class="contraband-count">1</span>
                      <span class="contraband-category">{{$t('personal-inspection.drug')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ff7e00">
                      <b-img src="/assets/img/knife_icon.svg" class="contraband-icon"/>
                      <span class="contraband-count">0</span>
                      <span class="contraband-category">{{$t('personal-inspection.dagger')}}</span>
                    </div>
                  </b-col>
                  <b-col cols="auto">
                    <div class="seized-item" style="background-color: #ffae00">
                      <b-img src="/assets/img/cigarette_icon.svg" class="contraband-icon"/>
                      <span class="contraband-count">9</span>
                      <span class="contraband-category">{{$t('personal-inspection.firearms')}}</span>
                    </div>
                  </b-col>
                </b-row>
              </b-col>
            </b-row>

            <b-row>
              <b-col cols="12" class="align-self-end text-right mt-3">
                <b-button size="sm" variant="orange default" @click="pageStatus='table'">
                  <i class="icofont-gift"></i>
                  {{ $t('personal-inspection.collection') }}
                </b-button>
                <b-button size="sm" variant="info default" @click="pageStatus='table'">
                  <i class="icofont-long-arrow-left"></i>
                  {{ $t('personal-inspection.return') }}
                </b-button>
              </b-col>
            </b-row>

          </b-card>
        </b-col>
      </b-row>
    </div>


  </div>
</template>

<style lang="scss">
  span.cursor-p {
    cursor: pointer !important;
  }

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

  .icon-container {
    font-size: 20px;

    .icofont-star {
      color: #ffe400;
    }

    .icofont-search-user {
      color: #ff9c0e;
    }

    .icofont-female {
      color: #fe687f;
    }
  }

  .control-group {
    display: flex;
    align-items: flex-start;

    .control-btn-wrapper {
      display: flex;
      flex-grow: 1;
      flex-wrap: wrap;

      .control-btn {
        width: calc(100% / 6);
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 24px;

        img {
          $size: 40px;
          width: $size;
          height: $size;
          margin-bottom: 6px;
        }

        span {
          display: block;
        }
      }
    }

    .switch-wrapper {
      width: 60px;
      height: 40px;
      display: flex;
      align-items: center;

      .separator {
        border: 0;
        width: 1px;
        height: 30px;
        background: #1e9dd2;
        flex-shrink: 0;
      }

      .switch {
        .vue-switcher {
          display: flex;
          height: 100%;
          margin: 0;
          transform: scale(0.8);
        }
      }
    }

    @media screen and (max-width: 1700px) {

      .control-btn-wrapper {
        .control-btn {
          img {
            $size: 28px;
            width: $size !important;
            height: $size !important;
          }
        }
      }
      .switch-wrapper {
        height: 28px;

        .separator {
          height: 28px;
        }
      }

    }
  }


  .history-chart {

    $ratio: 12.8;

    width: 100%;
    padding-bottom: 100% / $ratio;
    position: relative;

    margin-bottom: 24px;

    & > :first-child {
      left: 0;
      height: 100%;
      position: absolute;
      top: 0;
      width: 100%;

      background: url("/assets/img/history_chart.png") no-repeat;
      background-size: contain;


      $elements: 5;
      @for $i from 0 to $elements {
        .part:nth-child(#{$i + 1}) {
          position: absolute;
          top: 25%;
          bottom: 25%;
          left: 2% + 20% * $i;
          width: 20% - 4%;
          display: flex;
          color: white;
          align-items: center;
          justify-content: space-between;

          $date-color: #0c70ab;

          .top-date {
            color: $date-color;
            position: absolute;
            top: 104%;
            left: -6%;
          }

          .bottom-date {
            color: $date-color;
            position: absolute;
            bottom: 104%;
            right: 2%;
          }
        }
      }

    }


  }

  .evidence-gallery {
    .col-auto {
      padding-right: 0px;
    }
  }

  .seized-list {
    label {
      position: absolute;
      top: -26px;
      right: 291px;
    }

    .row {
      padding-bottom: 34px;

      .col-auto {
        padding-left: 0px;
        margin-left: -8px;
      }

      .seized-item {
        width: 80px;
        height: 62px;
        border-radius: 5px;

        .contraband-icon {
          position: absolute;
          top: 4px;
          right: 20px;
          width: 25px;
          height: 18px;
        }

        span.contraband-count {
          position: absolute;
          top: 13px;
          left: 32px;
          color: white;
          font-size: 1.5rem;
        }

        span.contraband-category {
          position: absolute;
          bottom: 1px;
          left: 4px;
          color: white
        }
      }
    }
  }

</style>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {getDirection} from "../../../utils";
  import _ from "lodash";
  import {getApiManager, getDateTimeWithFormat} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {validationMixin} from 'vuelidate';
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import Switches from 'vue-switches';
  import {LightGallery} from 'vue-light-gallery';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'light-gallery': LightGallery,
      'date-picker': DatePicker
    },
    mounted() {
      //this.$refs.taskVuetable.$parent.transform = this.transform.bind(this);
      this.getSiteOption();

    },
    data: function () {
      return {
        isExpanded: false,
        isCheckAll: false,
        pageStatus: 'table',
        filter: {
          taskNumber: null,
          mode: null,
          status: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
          // TODO: search filter
        },

        siteData: [],
        showPage: [],

        // TODO: select options
        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 4, text: '安检仪+审图端+手检端'},
          {value: 1, text: '安检仪+(本地手检)'},
          {value: 2, text: '安检仪+手检端'},
          {value: 3, text: '安检仪+审图端'},
        ],
        statusOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'pending_dispatch', text: this.$t('personal-inspection.pending-dispatch')},
          {value: 'pending_review', text: this.$t('personal-inspection.pending-review')},
          {value: 'while_review', text: this.$t('personal-inspection.while-review')},
          {value: 'pending_inspection', text: this.$t('personal-inspection.pending-inspection')},
          {value: 'while_inspection', text: this.$t('personal-inspection.while-inspection')}
        ],
        onSiteOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'pending-dispatch', text: this.$t('personal-inspection.task-pending-dispatch')},
          {value: 'dispatch', text: this.$t('personal-inspection.task-dispatched')},
          {value: 'while-review', text: this.$t('personal-inspection.while-review')},
          {value: 'reviewed', text: this.$t('personal-inspection.reviewed')},
          {value: 'while-inspection', text: this.$t('personal-inspection.while-inspection')},
        ],

        onSiteOption: [],

        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/history-task/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'historyId',
              title: this.$t('personal-inspection.serial-number'),
              sortField: 'task_id',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (historyId) => {
                if (historyId == null) return '';
                return historyId;
              }
            },
            {
              name: '__slot:task',
              title: this.$t('personal-inspection.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'

            },
            {
              name: '__slot:scanImage',
              title: this.$t('personal-inspection.image'),
              titleClass: 'text-center',
              dataClass: 'text-center'

            },
            {
              name: '__slot:mode',
              title: this.$t('personal-inspection.operation-mode'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'handTaskResult',
              title: this.$t('personal-inspection.task-result'),
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

                if (handTaskResult == null) return '';
                if (!dictionary.hasOwnProperty(handTaskResult)) return 'Invalid';
                return dictionary[handTaskResult];
              }
            },
            {
              name: 'task',
              title: this.$t('personal-inspection.on-site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (task) => {
                if (task == null) return '';
                if (task.field == null) return '';
                return task.field.fieldDesignation;
              }
            },
            {
              name: 'scanDevice',
              title: this.$t('personal-inspection.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanDevice) => {
                if (scanDevice == null) return '';
                return scanDevice.deviceName;
              }
            },
            {
              name: 'scanPointsman',
              title: this.$t('personal-inspection.guide'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanPointsman) => {
                if (scanPointsman == null) return '';
                return scanPointsman.userName;
              }
            },
            {
              name: 'scanStartTime',
              title: this.$t('personal-inspection.scan-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanStartTime) => {
                if (scanStartTime == null) return '';
                return getDateTimeWithFormat(scanStartTime);
              }
            },
            {
              name: 'scanEndTime',
              title: this.$t('personal-inspection.scan-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanEndTime) => {
                if (scanEndTime == null) return '';
                return getDateTimeWithFormat(scanEndTime);
              }
            },
            {
              name: 'judgeDevice',
              title: this.$t('personal-inspection.judgement-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeDevice) => {
                if (judgeDevice == null) return '';
                return judgeDevice.deviceName;
              }
            },
            {
              name: 'judgeUser',
              title: this.$t('personal-inspection.judge'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeUser) => {
                if (judgeUser == null) return '';
                return judgeUser.userName;
              }
            },
            {
              name: 'judgeStartTime',
              title: this.$t('personal-inspection.judgement-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeStartTime) => {
                if (judgeStartTime == null) return '';
                return getDateTimeWithFormat(judgeStartTime);
              }
            },
            {
              name: 'judgeEndTime',
              title: this.$t('personal-inspection.judgement-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (judgeEndTime) => {
                if (judgeEndTime == null) return '';
                return getDateTimeWithFormat(judgeEndTime);
              }
            },
            {
              name: 'handDevice',
              title: this.$t('personal-inspection.hand-check-station'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handDevice) => {
                if (handDevice == null) return '';
                return handDevice.deviceName;
              }
            },
            {
              name: 'handUser',
              title: this.$t('personal-inspection.hand-checker'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handUser) => {
                if (handUser == null) return '';
                return handUser.userName;
              }
            },
            {
              name: 'handStartTime',
              title: this.$t('personal-inspection.hand-check-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handStartTime) => {
                if (handStartTime == null) return '';
                return getDateTimeWithFormat(handStartTime);
              }
            },
          ],
          perPage: 10,
        },
        power: true,

        thumbs: [
          {name: '001.jpg', src: '/assets/img/drug-thumb.jpg'},
          {name: '001.jpg', src: '/assets/img/drug-thumb.jpg'},
          {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'},
          {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'},
          {name: '001.jpg', src: '/assets/img/glock-thumb.jpg'}
        ],
        images: [
          '/assets/img/drug.jpg',
          '/assets/img/drug.jpg',
          '/assets/img/glock.jpg',
          '/assets/img/glock.jpg',
          '/assets/img/glock.jpg',
        ],
        photoIndex: null


      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
      },
      'operatingLogTableItems.perPage': function (newVal) {
        this.$refs.operatingLogTable.refresh();
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
      onCheckEvent() {
        //this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}})
        let isCheck = this.isCheckAll;
        let cnt = this.$refs.taskVuetable.selectedTo.length;
        console.log(cnt);
        if (cnt === 0) {
          this.isCheckAll = false;
        } else {
          this.isCheckAll = true;
        }
        console.log(this.isCheckAll);

      },

      onGenerateExcelButton() {
        let str = "";
        if (this.isCheckAll === true) {
          str = "";
        } else {
          let cnt = this.$refs.taskVuetable.selectedTo.length;
          str = str + this.$refs.taskVuetable.selectedTo[0];
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {
            //console.log(this.$refs.taskVuetable.selectedTo[i]);
            str = str + "," + this.$refs.taskVuetable.selectedTo[i];
            //console.log(str);
          }
        }
        getApiManager()
          .post(`${apiBaseUrl}/task/history-task/generate/export`, {
            'isAll': this.isCheckAll,
            'filter': this.filter,
            'idList': str
          }, {
            responseType: 'blob'
          })
          .then((response) => {
            let fileURL = window.URL.createObjectURL(new Blob([response.data]));
            let fileLink = document.createElement('a');

            fileLink.href = fileURL;
            fileLink.setAttribute('download', 'History-Task.xlsx');
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
          let cnt = this.$refs.taskVuetable.selectedTo.length;
          str = str + this.$refs.taskVuetable.selectedTo[0];
          //for(int i =1 ; i < size; i ++) str = str + "," + value[i];
          for (let i = 1; i < cnt; i++) {
            //console.log(this.$refs.taskVuetable.selectedTo[i]);
            str = str + "," + this.$refs.taskVuetable.selectedTo[i];
            //console.log(str);
          }
        }
        getApiManager()
          .post(`${apiBaseUrl}/task/history-task/generate/print`, {
            'isAll': this.isCheckAll,
            'filter': this.filter,
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
      onRowClicked(taskNumber) {
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/task/history-task/get-one`, {
            'historyId': taskNumber,
          })
          .then((response) => {
            let message = response.data.message;

            console.log(message);

            switch (message) {
              case responseMessages['ok']: // okay
                this.showPage = response.data.data;
                break;

            }
          })
          .catch((error) => {
          });
        this.pageStatus = 'show';
      },
      getDateTimeFormat2(datatime) {
        return getDateTimeWithFormat(datatime);
      },
      getDateTimeFormat(datatime) {
        return getDateTimeWithFormat(datatime, 'monitor');
      },
      onSearchButton() {
        this.$refs.taskVuetable.refresh();
      },
      onResetButton() {

        this.filter = {
          taskNumber: null,
          mode: null,
          status: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
        };
        //this.$refs.taskVuetable.refresh();
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

      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,

          filter: this.filter,

          perPage: this.taskVuetableItems.perPage,
        });
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
      },
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page)
      }
    }
  }
</script>
