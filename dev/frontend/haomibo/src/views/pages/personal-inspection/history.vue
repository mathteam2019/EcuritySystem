<template>
  <div class="history-task">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-card class="main-without-tab" v-if="pageStatus === 'table'" style="margin-top: 20px;">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input v-model="filter.taskNumber"/>
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
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="8" v-if="isExpanded">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input v-model="filter.userName"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.start-time')">
                  <date-picker v-model="filter.startTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.end-time')">
                  <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""/>
                </b-form-group>
              </b-col>
              <b-col/>
              <b-col/>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('history_task_export')" @click="showModal()">
                <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export')}}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('history_task_print')" @click="onPrintButton()">
                <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="taskVuetable"
                track-by="historyId"
                :api-url="taskVuetableItems.apiUrl"
                :fields="taskVuetableItems.fields"
                :http-fetch="taskVuetableHttpFetch"
                :per-page="taskVuetableItems.perPage"
                pagination-path="pagination"
                class="table-hover"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="task" slot-scope="props">
                    <span v-if="props.rowData.historyId!=null" class="cursor-p text-primary"
                          @click="onRowClicked(props.rowData.historyId)">
                      {{props.rowData.task.taskNumber}}
                    </span>
                  <span v-else> </span>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="props.rowData.workMode==null"></div>

                  <div v-else>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('all')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan+judge')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workMode.modeName===getModeDataCode('scan+hand')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                  </div>
                </template>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="taskVuetablePagination"
                :initial-per-page="taskVuetableItems.perPage"
                @vuetable-pagination:change-page="onTaskVuetableChangePage"
                @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <div v-if="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col cols="4" style="padding-left: 13px; padding-right: 8px">
          <b-card class="h-100">
            <div style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 5px;"/>
            <div>
              <div style="font-size: 15px; font-weight: bold; margin-bottom: 10px;">{{$t('personal-inspection.scanned-image')}}</div>
            </div>
            <b-row class="mb-1">
              <b-col style="margin-bottom: 6px;" class="icon-container">
                <div v-if="showPage.workMode==null"></div>

                <div v-else>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('all')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan+judge')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workMode.modeName===getModeDataCode('scan+hand')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                </div>
              </b-col>
              <b-col style="margin-bottom: 5px;" class="text-right icon-container">
                <span v-if="showPage.serKnowledgeCase!=null && showPage.serKnowledgeCase.caseId!=null"><i class="icofont-star"/></span>
                <span v-if="showPage.judgeResult!=null && showPage.judgeResult==='TRUE'"><i class="icofont-search-user"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000002'"><i class="icofont-female"/></span>
              </b-col>
            </b-row>
            <b-row style="margin-bottom: 0.5rem;">
              <b-col style="padding-right: 0.5rem; padding-left: 1.5rem;">
                <canvas id="firstcanvas" style="height: 23vw;" class="img-fluid w-100 "/>
              </b-col>
              <b-col style="padding-right: 1.5rem; padding-left: 0.5rem;">
                <canvas id="secondcanvas"  style="height: 23vw;" class="img-fluid w-100 "/>
                <div style="width: 100%; height: 24px;" class="text-right icon-container">
                  <div v-if="power===true">
                    <b-img :disabled="power===true" src="/assets/img/previous_cartoon.png" class="operation-icon"
                           @click="previousImage()"/>
                    <b-img src="/assets/img/next_cartoon.png" class="operation-icon" @click="nextImage()"/>
                  </div>
                </div>
              </b-col>
            </b-row>
            <b-row style="float: right;">
              <b-col class="control-group">
                <div class="control-btn-wrapper">
                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png" @click="onlyOneSlide(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png" @click="onlyOneSlide(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png" @click="filterId(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png" @click="filterId(3)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png" @click="filterId(4)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png" @click="filterId(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png" @click="filterId(12)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(0)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(9)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(10)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png" @click="filterId(13)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>
                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png" @click="loadImage()"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>
                <div class="switch-wrapper">
                  <div class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" :disabled="checkPermItem('history_task_toggle')" theme="custom" color="info"/>
                  </div>
                </div>
              </b-col>
              <b-col cols="8" v-if="isSlidebar2Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar2value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
              <b-col cols="8" v-if="isSlidebar1Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar1value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
            </b-row>
          </b-card>
        </b-col>
        <b-col cols="8" style="padding-right: 13px">
          <b-card class="h-100 d-flex flex-column right-card">
            <div style="height: 20px;">
              <div style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
              <div style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.history')}}</div>
            </div>
            <div class="history-chart">
              <div>
                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.start')}}</div>
                  </div>
                  <div class="right">
                    <div>Start</div>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.scan')}}</div>
                    <div>
                      <div v-if="showPage.scanPointsmanName != null">{{showPage.scanPointsmanName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Scanning</div>
                  </div>
                  <div class="top-date">
                    <label
                      v-if="showPage.scanStartTime != null">{{this.getDateTimeFormat2(showPage.scanStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.scanEndTime != null">{{this.getDateTimeFormat2(showPage.scanEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.judge')}}</div>
                    <div>
                      <div v-if="showPage.judgeUser != null">{{showPage.judgeUser.userName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Decision</div>
                    <div>diagram</div>
                  </div>
                  <div class="top-date">
                    <label v-if="showPage.judgeStartTime==null"/>

                    <label v-else-if="showPage.workMode.modeName===getModeDataCode('scan+judge') || showPage.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.judgeStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.judgeEndTime==null"/>
                    <label
                      v-else-if="showPage.workMode.modeName===getModeDataCode('scan+judge') || showPage.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.judgeEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('device-config.maintenance-config.inspection')}}</div>
                    <div>
                      <div v-if="showPage.handUser == null"></div>
                      <div v-else>{{showPage.handUser.userName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Inspection</div>
                  </div>
                  <div class="top-date">
                    <label v-if="showPage.handStartTime == null"/>

                    <label v-else-if="showPage.workMode.modeName===getModeDataCode('scan+hand') || showPage.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.handStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.handEndTime == null"/>

                    <label v-else-if="showPage.workMode.modeName===getModeDataCode('scan+hand') || showPage.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.handEndTime)}}</label>
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
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.task == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else v-model="showPage.task.taskNumber"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-if="showPage.task==null" style="background-color: whitesmoke; border: none;"/>
                  <b-form-input disabled v-else-if="showPage.task.field==null" style="background-color: whitesmoke; border: none;"/>
                  <b-form-input disabled v-else v-model="showPage.task.field.fieldDesignation" style="background-color: whitesmoke; border: none;"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.scanDevice == null"/>
                  <b-form-input disabled v-else v-model="showPage.scanDevice.deviceName" style="background-color: whitesmoke; border: none;"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.serScan == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else :value="getOptionValue(showPage.serScan.scanImageGender)"/>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.handDevice == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else v-model="showPage.handDevice.deviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.judgeDevice == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else v-model="showPage.judgeDevice.deviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.workMode==null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else :value="getModeName(showPage.workMode.modeName)"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-if="showPage.judgeResult == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" v-else :value="getOptionValue(showPage.judgeResult)"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; border: none;" :value="getOptionValue(showPage.handAppraise)"/>
                </b-form-group>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    备注
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled style="background-color: whitesmoke; max-width: 100%; border: none;" v-if="showPage.note == null"/>
                  <b-form-input disabled style="background-color: whitesmoke; max-width: 100%; border: none;" v-else v-model="showPage.note"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <div style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="justify-content-start" style="margin-bottom: 1rem; margin-top: 0.5rem">
                  <b-col>
                    <div v-if="handGoodExpanded[0]" class="text-center"  style="background-color: #ff0000; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[0]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[1]" class="text-center" style="background-color: #ff4e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[1]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[2]" class="text-center" style="background-color: #ff7e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[2]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[3]" class="text-center" style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[3]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[4]" class="text-center" style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[4]].text}}</span>
                    </div>
                  </b-col>
                </b-row>
                <div style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery" style="margin-top: 0.5rem">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`"
                         @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 60px; height: 45px;" :alt="thumb.name"/>

                  </b-col>
                  <b-col cols="auto" v-for="(video, videoIndex) in videos" :key="`video_${videoIndex}`"
                         @click="onVideoClick(video)">
                    <video style=" width: 50px; height: 40px;">
                      <source :src="video.src" type="video/mp4">
                    </video>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()"/>
                </b-row>
              </b-col>
              <b-col style="max-width: 45%;">
                <b-row>
                  <b-col v-if="getLocale()==='zh'" cols="12" class="align-self-end text-right mt-3">
                    <div v-if="showPage.handResult !== null">
                      <b-img v-if="showPage.handResult === 'TRUE'" src="/assets/img/icon_invalid.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                      <b-img v-if="showPage.handResult === 'FALSE'" src="/assets/img/icon_valid.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                    </div>
                    <div v-else>
                      <b-img v-if="showPage.judgeResult === 'TRUE'" src="/assets/img/icon_invalid.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                      <b-img v-if="showPage.judgeResult === 'FALSE'" src="/assets/img/icon_valid.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                    </div>
                  </b-col>
                  <b-col v-if="getLocale()==='en'" cols="12" class="align-self-end text-right mt-3">
                    <div v-if="showPage.handResult !== null">
                      <b-img v-if="showPage.handResult === 'TRUE'" src="/assets/img/icon_invalid_en.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                      <b-img v-if="showPage.handResult === 'FALSE'" src="/assets/img/icon_valid_en.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                    </div>
                    <div v-else>
                      <b-img v-if="showPage.judgeResult === 'TRUE'" src="/assets/img/icon_invalid_en.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                      <b-img v-if="showPage.judgeResult === 'FALSE'" src="/assets/img/icon_valid_en.png"
                             class="align-self-end" style="width: 100px; height: 95px;"/>
                    </div>
                  </b-col>
                </b-row>
                <b-row style="margin-top: 0.5rem">
                  <b-col cols="12" class="align-self-end text-right mt-3">
                    <b-button size="sm" variant="orange default" :disabled="checkPermItem('history_task_save')" :hidden="showPage.serKnowledgeCase!=null && showPage.serKnowledgeCase.caseId!=null" @click="showCollectionView()">
                      <i class="icofont-gift"/>
                      {{ $t('personal-inspection.collection') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onRowClicked(history_id)">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.refresh') }}
                    </b-button>
                    <b-button size="sm" variant="info default" @click="pageStatus='table'">
                      <i class="icofont-long-arrow-left"/>
                      {{ $t('personal-inspection.return') }}
                    </b-button>
                  </b-col>
                </b-row>

              </b-col>
            </b-row>
          </b-card>
        </b-col>
      </b-row>
      <div class="video-wrapper" v-show="showVideo">
        <div class="video-container">
          <video-player ref="videoPlayer" :options="videoOptions"/>
        </div>
        <span class="switch-action" @click="finishVideoShow()"><i class="icofont-close-line"/></span>
      </div>
      <b-modal  centered id="model-collection" ref="model-collection">
        <b-row>
          <b-col cols="12" class="d-flex justify-content-center">
            <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">標簽</h3>
          </b-col>
        </b-row>
        <b-row style="height : 100px;">
          <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
            <b-form-group class="mw-100 w-100" label="標簽">
              <v-select v-model="collectionLabel" :options="collectionLabelOptions"
                        class="v-select1" multiple :searchable="false" :dir="direction"/>
            </b-form-group>
          </b-col>
        </b-row>
        <div slot="modal-footer">
          <b-button size="sm" variant="orange default" @click="onCollectionClicked(history_id)">
            <i class="icofont-gift"/>
            {{ $t('personal-inspection.collection') }}
          </b-button>
          <b-button size="sm" variant="light default" @click="hideModal('model-collection')">
            <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
          </b-button>
        </div>
      </b-modal>
      <b-modal  centered id="model-export" ref="model-export">
        <b-row>
          <b-col cols="12" class="d-flex justify-content-center">
            <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export') }}</h3>
          </b-col>
        </b-row>
        <b-row style="height : 100px;">
          <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
            <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
              <v-select v-model="fileSelection" :options="fileSelectionOptions"
                        :state="!$v.fileSelection.$invalid" :searchable="false"
                        class="v-select-custom-style" :dir="direction" multiple/>
            </b-form-group>
          </b-col>
        </b-row>
        <div slot="modal-footer">
          <b-button size="sm" variant="orange default" @click="onExport()">
            <i class="icofont-gift"/>
            {{ $t('permission-management.export') }}
          </b-button>
          <b-button size="sm" variant="light default" @click="hideModal('model-export')">
            <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
          </b-button>
        </div>
      </b-modal>
      <Modal
        ref="exportModal"
        :show="isModalVisible"
        :link="link" :params="params" :name="name"
        @close="closeModal"
      />
    </div>
  </div>
</template>
<style lang="scss">

  .modal .modal-header {
    padding: 1.25rem;
  }

  .vc-chrome {
    position: absolute;
    top: calc(2rem + 4px);
    right: 0;
    z-index: 9;
  }

  .current-color {
    display: inline-block;
    width: 1rem;
    height: 1rem;
    background-color: #000;
    cursor: pointer;
  }
  span.cursor-p {
    cursor: pointer !important;
  }

  .slide-class{
    margin-top: -30px;
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
    margin-right: 8px;
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
        margin-bottom: 5px;

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

      background: url("../../../assets/img/history_chart.png") no-repeat;
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
        width: 100px;
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
          position: center;
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
  import vSelect from 'vue-select';
  import {getApiManager, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import 'vue-tree-halower/dist/halower-tree.min.css'
  import Switches from 'vue-switches';
  import {LightGallery} from 'vue-light-gallery';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {loadImageCanvas, getLoginInfo, imageFilterById, getLocale, getDirection} from '../../../utils'
  import VueSlideBar from 'vue-slide-bar'
  import {checkPermissionItem} from "../../../utils";
  import Videoplayer from '../../../components/Common/VideoPlayer';
  import 'vue-select/dist/vue-select.css'
  import ExportModal from '../../../components/Modal/ExportModal'
  import Modal from '../../../components/Modal/modal'

  import {validationMixin} from 'vuelidate';

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'light-gallery': LightGallery,
      'date-picker': DatePicker,
      'video-player': Videoplayer,
      'export-modal':ExportModal,
      VueSlideBar,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },
    mounted() {
      this.getSiteOption();
    },
    data() {
      return {
        link: '',
        params:{},
        name:'',
        isModalVisible: false,
        isExpanded: false,
        pageStatus: 'table',
        power: false,
        siteData: [],
        showPage: [],
        fileSelection : [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        showExportModal: false,

        isSlidebar1Expended: false,
        isSlidebar2Expended: false,
        slidebar1value: 0,
        slidebar2value: 0,

        slider: {
          lineHeight: 10,
          processStyle: {
            backgroundColor: 'blue'
          }
        },

        validIcon: null,

        task_id: null,

        apiBaseURL: '',

        thumbs: [],
        images: [],
        videos: [],
        photoIndex: null,
        showVideo: false,

        widthRate: [],
        heightRate: [],
        imgRect: [],
        cartoonRect: [],
        rRects: [],
        rectAdd: [],
        rectDel: [],
        imageUrls: [],

        imagesInfo: [],
        cartoonsInfo: [],
        imageRectR: [],
        imageRectL: [],
        cartoonRectR: [],
        cartoonRectL: [],
        cntCartoon: 0,
        orderCartoon: 0,

        videoOptions: {
          autoplay: true,
          language: 'zh',//todo need to set that lang setting with multiple.
          poster: '/assets/img/glock-thumb.jpg', //todo need to set its image data differently if needed
          sources: [{
            type: "video/mp4",
            src: '/assets/img/113.mp4',
          }],
        },
        selectedVideo: null,

        history_id:0,
        collectionLabel: [],
        direction: getDirection().direction,
        collectionLabelOptions: [
          {value: '1000002701', label: '好'},
          {value: '1000002702', label: '良'},
          {value: '1000002703', label: '差'},
          {value: '1000002704', label: '非常差'}
        ],
        collectionLabelOption:[],

        isCheckAll: false,
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

        handGoodDataCode:['1000001601', '1000001602', '1000001603', '1000001604', '1000001605'],
        handGoodExpanded:[false, false, false, false, false],
        handGoodDataCodeExpanded:[],
        handGoodDataCodeValue:{
          1000001601:{text:'安眠药'},
          1000001602:{text:'仿真枪'},
          1000001603:{text:'玩具枪'},
          1000001604:{text:'气枪'},
          1000001605:{text:'打火机'},
        },
        // TODO: select options
        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '4', text: '安检仪+审图端+手检端'},
          {value: '1', text: '安检仪+(本地手检)'},
          {value: '2', text: '安检仪+手检端'},
          {value: '3', text: '安检仪+审图端'},
        ],
        statusOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001102', text: this.$t('maintenance-management.process-task.dispatch')},
          {value: '1000001103', text: this.$t('maintenance-management.process-task.judge')},
          {value: '1000001104', text: this.$t('maintenance-management.process-task.hand')},
          {value: '1000001106', text: this.$t('maintenance-management.process-task.scan')}
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
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (historyId) => {
                if (historyId == null) return '';
                return historyId;
              }
            },
            {
              name: '__slot:task',
              sortField: 'taskNumber',
              title: this.$t('personal-inspection.task-number'),
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

                  "TRUE": `<span style="color:#ef6e69;">${this.$t('knowledge-base.seized')}</span>`,
                  "FALSE": `<span style="color:#e8a23e;">${this.$t('knowledge-base.no-seized')}</span>`,

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
              sortField : 'scanStartTime',
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
              sortField : 'scanEndTime',
              title: this.$t('personal-inspection.scan-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanEndTime) => {
                if (scanEndTime == null) return '';
                return getDateTimeWithFormat(scanEndTime);
              }
            },
          ],
          perPage: 10,
        },
      }
    },

    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
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

      power(newValue) {

        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.loadImage();

      },

      slidebar1value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(5);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(6);
          }
        }
      },
      slidebar2value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(7);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(8);
          }
        }
      },
    },

    methods: {
      showModal() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        this.link = `task/history-task/generate`;
        this.name = 'History-Task';
        this.isModalVisible = true;
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      onVideoClick(video) {
        this.videoOptions.sources.src = video.src;
        this.$refs.videoPlayer.initialize();
        this.showVideo = true;
      },

      finishVideoShow() {
        this.showVideo = false;
        this.$refs.videoPlayer.dispose();
      },

      onThumbClick(index) {
        this.photoIndex = index;
        this.isOpen = true;
      },

      handleHide() {
        this.photoIndex = null;
        this.isOpen = false;
      },

      onlyOneSlide(value) {
        if (this.power === false) {
          if (value === 1) {
            this.isSlidebar1Expended = !this.isSlidebar1Expended;
            this.isSlidebar2Expended = !this.isSlidebar1Expended;
          }
          if (value === 2) {
            this.isSlidebar2Expended = !this.isSlidebar2Expended;
            this.isSlidebar1Expended = !this.isSlidebar2Expended;
          }
        }
      },

      filterId(id) {
        if(id<5||id>8) {
          this.isSlidebar1Expended = false;
          this.isSlidebar2Expended = false;
        }
        if (this.power === false) {
          imageFilterById(id, this.imageRectL, this.imageRectR);
        }
      },

      loadImage() {
        let url1 = '';
        let url2 = '';
        if (this.power === false) {
          this.slidebar1value = 0;
          this.slidebar2value = 0;
          if (this.imagesInfo[0] !== undefined) {
            url1 = this.imagesInfo[0].imageUrl;
          }
          if (this.imagesInfo[1] !== undefined) {
            url2 = this.imagesInfo[1].imageUrl;
          }
          loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

        } else {
          let k = this.orderCartoon * 2;
          this.cartoonRectL = [];
          this.cartoonRectR = [];

          if (this.cartoonsInfo[k] !== undefined) {
            url1 = this.cartoonsInfo[k].imageUrl;
            for (let i = 0; i < this.cartoonsInfo[k].imageRect.length; i++) {
              this.cartoonRectL.push({
                x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].x,
                y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].y,
                width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].width,
                height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].height,
                colour: this.cartoonsInfo[k].colorRect,
              });
            }

            for (let i = 0; i < this.cartoonsInfo[k].rectsAdd.length; i++) {
              this.cartoonRectL.push({
                x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].x,
                y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].y,
                width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].width,
                height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].height,
                colour: this.cartoonsInfo[k].colorAdd,
              });
            }

            if (this.cartoonsInfo[k].displayDel === '1000000601') {
              for (let i = 0; i < this.cartoonsInfo[k].rectsDel.length; i++) {
                this.cartoonRectL.push({
                  x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].x,
                  y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].y,
                  width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].width,
                  height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].height,
                  colour: this.cartoonsInfo[k].colorDel,
                });
              }
            }
          }

          if (this.cartoonsInfo[k + 1] !== undefined) {
            url2 = this.cartoonsInfo[k + 1].imageUrl;
            for (let i = 0; i < this.cartoonsInfo[k + 1].imageRect.length; i++) {
              this.cartoonRectR.push({
                x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].x,
                y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].y,
                width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].width,
                height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].height,
                colour: this.cartoonsInfo[k + 1].colorRect,
              });
            }

            for (let i = 0; i < this.cartoonsInfo[k + 1].rectsAdd.length; i++) {
              this.cartoonRectR.push({
                x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].x,
                y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].y,
                width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].width,
                height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].height,
                colour: this.cartoonsInfo[k + 1].colorAdd,
              });
            }

            if (this.cartoonsInfo[k + 1].displayDel === '1000000601') {
              for (let i = 0; i < this.cartoonsInfo[k + 1].rectsDel.length; i++) {
                this.cartoonRectR.push({
                  x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].x,
                  y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].y,
                  width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].width,
                  height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].height,
                  colour: this.cartoonsInfo[k + 1].colorDel,
                });
              }
            }
          }

          loadImageCanvas(url1, url2, this.cartoonRectL, this.cartoonRectR, this.power);

        }

      },

      previousImage() {
        if (this.orderCartoon > 0) {
          this.orderCartoon = this.orderCartoon - 1;
          this.loadImage();
        }
      },

      nextImage() {
        if (this.orderCartoon < this.cntCartoon - 1) {
          this.orderCartoon = this.orderCartoon + 1;
          this.loadImage();
        }
      },

      getOptionValue(dataCode) {
        const dictionary = {
          "1000000001": `${this.$t('permission-management.male')}`,
          "1000000002": `${this.$t('permission-management.female')}`,
          "1000000601": `${this.$t('system-setting.parameter-setting.yes')}`,
          "1000000602": `${this.$t('system-setting.parameter-setting.no')}`,
          "1000001701": `${this.$t('permission-management.timeout')}`,
          "1000001702": `${this.$t('permission-management.timein')}`,
          "TRUE": `${this.$t('knowledge-base.suspect')}`,
          "FALSE": `${this.$t('knowledge-base.no-suspect')}`,
          "1000001301": `${this.$t('permission-management.female')}`,
          "1000001302": `${this.$t('permission-management.female')}`,
          "1000001303": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001304": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001102": `${this.$t('maintenance-management.process-task.dispatch')}`,
          "1000001103": `${this.$t('maintenance-management.process-task.judge')}}`,
          "1000001104": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001106": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001201": `${this.$t('maintenance-management.process-task.system')}`,
          "1000001202": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000001801": `${this.$t('maintenance-management.process-task.underreport')}`,
          "1000001802": `${this.$t('maintenance-management.process-task.falsepositive')}`
        };
        if (!dictionary.hasOwnProperty(dataCode)) return '';
        return dictionary[dataCode];
      },

      getModeDataCode(value){
        const dictionary = {

          "scan": `1000001301`,
          "scan+hand": `1000001302`,
          "scan+judge": `1000001303`,
          "all": `1000001304`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getModeName(value){
        const dictionary = {

          "1000001301": `安检仪+(本地手检)`,
          "1000001302": `安检仪+手检端`,
          "1000001303": `安检仪+审图端`,
          "1000001304": `安检仪+审图端+手检端`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getTagListLabel(value){
        const dictionary = {

          "1000002701": `好`,
          "1000002702": `良`,
          "1000002703": `差`,
          "1000002704": `非常差`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      onExportButton() {
        this.fileSelection = [];
        this.$refs['model-collection'].show();
      },
      onExport(){
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `task/history-task/generate`;
        if(checkedIds.length>0  && this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'History-Task',  this.fileSelection);
          this.hideModal('model-export')
        }
      },

      onPrintButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };

        let link = `task/history-task/generate`;
        if(checkedIds.length>0) {
          printFileFromServer(link, params);
        }
      },

      getSiteOption() {
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              this.apiBaseURL = apiBaseUrl;
              break;
          }
        })
          .catch((error) => {
          });

      },

      getLocale(){
        return getLocale();
      },

      onRowClicked(taskNumber) {
        this.pageStatus = 'show';
        this.power = false;
        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.cntCartoon = 0;
        this.orderCartoon = 0;
        this.collectionLabel = [];

        let url1 = '';
        let url2 = '';
        let deviceImage, submitRects;
        let colourInfo = null;
        let handGood = null, handAttached = null;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/task/history-task/get-one`, {
            'historyId': taskNumber,
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.showPage = response.data.data;
                this.apiBaseURL = apiBaseUrl;
                this.thumbs = [];
                this.videos = [];
                this.images =[];
                this.imgRect = [];
                this.cartoonRect = [];
                this.rRects = [];
                this.imagesInfo = [];
                this.cartoonsInfo = [];
                this.imageRectR = [];
                this.imageRectL = [];
                this.cartoonRectR = [];
                this.cartoonRectL = [];
                this.collectionLabel=[];
                this.handGoodExpanded=[];
                this.handGoodDataCodeExpanded = [];

                deviceImage=[];
                submitRects=[];
                colourInfo = this.showPage.platFormCheckParams;

                if(this.showPage.serScan!==undefined && this.showPage.serScan!==null) {
                  if(this.showPage.serScan.scanDeviceImages!== null) {
                    deviceImage = this.showPage.serScan.scanDeviceImages;
                    deviceImage = JSON.parse(deviceImage);
                  }
                }

                if(this.showPage.serJudgeGraph!==undefined && this.showPage.serJudgeGraph!==null) {
                  if(this.showPage.serJudgeGraph.judgeSubmitrects!==null) {
                    submitRects = this.showPage.serJudgeGraph.judgeSubmitrects;
                    submitRects = JSON.parse(submitRects);
                  }
                }

                this.cntCartoon = Math.floor(deviceImage.length / 2);

                if(deviceImage!==null&& submitRects!==null) {
                  for (let i = 0; i < deviceImage.length; i++) {
                    if (i < 2) {
                      this.imagesInfo.push({
                        rateWidth: 168 / deviceImage[i].width,
                        rateHeight: 300 / deviceImage[i].height,
                        imageUrl: deviceImage[i].image,
                        imageRect: deviceImage[i].imageRects,
                        colorRect: colourInfo.scanRecogniseColour
                      });
                    }

                    if (submitRects[i] !== undefined && submitRects[i] !== null) {
                      this.cartoonsInfo.push({
                        rateWidth: 168 / deviceImage[i].width,
                        rateHeight: 300 / deviceImage[i].height,
                        imageUrl: deviceImage[i].cartoon,
                        imageRect: deviceImage[i].cartoonRects,
                        colorRect: colourInfo.scanRecogniseColour,
                        colorAdd: colourInfo.judgeRecogniseColour,
                        colorDel: colourInfo.displayDeleteSuspicionColour,
                        displayDel: colourInfo.displayDeleteSuspicion,
                        rectsAdd: submitRects[i].rectsAdded,
                        rectsDel: submitRects[i].rectsDeleted
                      });
                    }
                  }
                }

                if (this.imagesInfo[0] !== undefined) {
                  for (let i = 0; i < this.imagesInfo[0].imageRect.length; i++) {
                    this.imageRectL.push({
                      x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].x,
                      y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].y,
                      width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].width,
                      height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].height,
                      colour: this.imagesInfo[0].colorRect,
                    });
                  }
                  url1 = this.imagesInfo[0].imageUrl;
                }

                if (this.imagesInfo[1] !== undefined) {
                  for (let i = 0; i < this.imagesInfo[1].imageRect.length; i++) {
                    this.imageRectR.push({
                      x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].x,
                      y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].y,
                      width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].width,
                      height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].height,
                      colour: this.imagesInfo[1].colorRect,
                    });
                  }
                  url2 = this.imagesInfo[1].imageUrl;
                }

                loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

                let handGoodsStr = this.showPage.task.serCheckResult.handGoods;
                let handAttactedStr = this.showPage.task.serCheckResult.handAttached;

                if(handGoodsStr!==null && handAttactedStr!==null) {
                  handGood = handGoodsStr.split(",");
                  handAttached = handAttactedStr.split(",");
                }
                let k=0;
                if(handGood !==null) {
                  for (let i = 0; i < handGood.length; i++) {
                    for (let j = 0; j < 5; j++) {
                      if (handGood[i] === this.handGoodDataCode[j]) {
                        this.handGoodExpanded[k] = true;
                        this.handGoodDataCodeExpanded[k] = this.handGoodDataCode[j];
                        k++;
                      }
                    }
                  }
                }


                //getting media data from server.
                if(handAttached !==null) {
                  for (let i = 0; i < handAttached.length; i++) {
                    let iHandAttached = handAttached[i].split(".");
                    if (iHandAttached[1] === "png" || iHandAttached[1] === "jpg") {
                      this.thumbs.push({
                        name: iHandAttached[0],
                        src: handAttached[i]
                      });
                      this.images.push(handAttached[i]);
                      /* this.thumbs[k].name = iHandAttached[0];
                    this.thumbs[k].src = handAttached[i];
                    this.images[k] = handAttached[i];*/

                    } else {
                      this.videos.push({
                        name: iHandAttached[0],
                        src: handAttached[i],
                        poster: '',//todo if client need to show different poster for each videos, should get its poster image from server.
                      });
                    }
                  }
                }

                break;// okay
            }
          })
          .catch((error) => {
          });

        this.history_id = taskNumber;

      },

      showCollectionView() {
        this.collectionLabel = [];
        this.$refs['model-collection'].show();
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onCollectionClicked(history_id){

        let tagList = [];
        for(let i=0; i<this.collectionLabel.length; i++){
          tagList[i] = this.collectionLabel[i].value;
        }
        let loginfo = getLoginInfo();
        let userId = loginfo.user.id;
        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/create`, {
            'historyId': history_id,
            'userId' :userId,
            'tagList' :tagList
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.$notify('success', this.$t('permission-management.success'), this.$t(`personal-inspection.activate`), {
                  duration: 3000,
                  permanent: false
                });// okay
                this.hideModal('model-collection');
                break;

            }
          })
          .catch((error) => {
          });
      },

      getDateTimeFormat2(dataTime) {
        if(dataTime==null){
          return '';
        }
        return getDateTimeWithFormat(dataTime);
      },

      getDateTimeFormat(dataTime) {
        if(dataTime==null){
          return '';
        }
        return getDateTimeWithFormat(dataTime, 'monitor');
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
          transformed.data.push(temp);
        }

        return transformed

      },

      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          filter: this.filter,
          sort:httpOptions.params.sort,
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
