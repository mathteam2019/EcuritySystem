<template>
<div class="dashboard-container d-flex flex-column">
  <b-row>
    <b-col cols="12">
      <b-img src="/assets/img/home_main_banner.png" fluid-grow class="shadow-sm" />
    </b-col>
  </b-row>
  <b-row class="d-flex flex-grow-1 align-items-center kcb-mt-20">
    <b-col cols="5">
      <b-img src="/assets/img/home_bottom_banner.png" fluid-grow class="shadow-sm" />
    </b-col>
    <b-col cols="7" class="pt-3 pl-5">
      <b-row>
        <b-col cols="6" class="article d-flex">
          <div class="article-icon-container">
            <b-img src="/assets/img/rocket_icon.svg" />
          </div>
          <div class="ml-5 mt-2 mr-3">
            <h5>{{$t('dashboard.article-title-1')}}</h5>
            <h6 v-html="$t('dashboard.article-extract-1')"></h6>
          </div>
        </b-col>
        <b-col cols="6" class="article d-flex">
          <div class="article-icon-container">
            <b-img src="/assets/img/rocket_icon.svg" />
          </div>
          <div class="ml-5 mt-2 mr-3">
            <h5>{{$t('dashboard.article-title-2')}}</h5>
            <h6 v-html="$t('dashboard.article-extract-2')"></h6>
          </div>
        </b-col>
      </b-row>
      <b-row class="mt-5">
        <b-col cols="6" class="article d-flex">
          <div class="article-icon-container">
            <b-img src="/assets/img/rocket_icon.svg" />
          </div>
          <div class="ml-5 mt-2 mr-3">
            <h5>{{$t('dashboard.article-title-3')}}</h5>
            <h6 v-html="$t('dashboard.article-extract-3')"></h6>
          </div>
        </b-col>
        <b-col cols="6" class="article d-flex">
          <div class="article-icon-container">
            <b-img src="/assets/img/rocket_icon.svg" />
          </div>
          <div class="ml-5 mt-2 mr-3">
            <h5>{{$t('dashboard.article-title-4')}}</h5>
            <h6 v-html="$t('dashboard.article-extract-4')"></h6>
          </div>
        </b-col>
      </b-row>
    </b-col>
  </b-row>
  </div>
</template>

