<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.organization-management')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('permission-management.organization-table')">
        <b-row>
          <b-colxx xxs="12" lg="4" class="mb-4">
            <b-card class="mb-4" no-body>
              <div class="position-absolute card-top-buttons">
                <b-button variant="outline-white" class="icon-button"><i class="simple-icon-pencil"/></b-button>
              </div>
              <img src="/assets/img/detail.jpg" alt="Detail" class="card-img-top"/>
              <b-card-body>
                <p class="text-muted text-small mb-2">{{ $t('pages.description') }}</p>
                <p class="mb-3">
                  It’s all about simplicity…Less is more. Chocolate Cake
                  exclusively brings you the classic chocolate cake.
                  This cake is the one you always dream of-moist cake
                  and creamy chocolate frosting.
                  <br/>
                  <br/> This cake proudly serves itself for a family
                  gathering, a dinner party, a birthday celebration, a
                  baby christening, and a gift to someone special or
                  simply to have on hand on the cake stand at home
                  served with an ice cold glass of milk!
                </p>
                <p class="text-muted text-small mb-2">{{ $t('pages.rating') }}</p>
                <div class="mb-3">
                  <stars value="4" :disabled=true></stars>
                </div>
                <p class="text-muted text-small mb-2">{{ $t('pages.price') }}</p>
                <p class="mb-3">$8,14</p>
                <p class="text-muted text-small mb-2">{{ $t('pages.ingredients') }}</p>
                <div class="mb-3">
                  <p class="d-sm-inline-block mb-1">
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Flour</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Chocolate</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Caster Sugar</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Baking Powder</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Milk</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Eggs</b-badge>
                    <b-badge pill variant="outline-secondary" class="mb-1 mr-1">Vegetable Oil</b-badge>
                  </p>
                </div>
                <p class="text-muted text-small mb-2">{{ $t('pages.is-vegan') }}</p>
                <p>No</p>
              </b-card-body>
            </b-card>
            <radial-progress-card :title="$t('pages.order-status')" :percent="85" no-suffle class="mb-4"/>
            <radial-progress-card :title="$t('pages.bake-progress')" :percent="40" no-suffle class="mb-4"/>
          </b-colxx>
          <b-colxx xxs="12" lg="8">
            <b-row v-if="isLoad">
              <b-colxx xxs="6" class="mb-4">
                <small-line-chart-card class="dashboard-small-chart-analytics" label-prefix="$"
                                       :data="smallChartData1"/>
              </b-colxx>
              <b-colxx xxs="6" class="mb-4">
                <small-line-chart-card class="dashboard-small-chart-analytics" label-prefix="$"
                                       :data="smallChartData2"/>
              </b-colxx>
              <b-colxx xxs="6" class="mb-4">
                <small-line-chart-card class="dashboard-small-chart-analytics" label-prefix="$"
                                       :data="smallChartData3"/>
              </b-colxx>
              <b-colxx xxs="6" class="mb-4">
                <small-line-chart-card class="dashboard-small-chart-analytics" label-prefix="$"
                                       :data="smallChartData4"/>
              </b-colxx>
            </b-row>
            <b-card class="mb-4" :title="$t('pages.popularity')" v-if="isLoad">
              <div class="chart-container">
                <area-shadow-chart :data="areaChartData" :height="300"/>
              </div>
            </b-card>
            <b-card class="mb-4" :title="$t('pages.comments')">
              <comment-item v-for="(comment,index) in comments" :key="index" :data="comment" detail-path="#"/>
            </b-card>
          </b-colxx>
        </b-row>
      </b-tab>

      <b-tab :title="$t('pages.orders')">
        <b-row>
          <b-colxx>
            <order-item v-for="(order,index) in orders" :key="index" :data="order" detail-path="#"/>
          </b-colxx>
        </b-row>
      </b-tab>
    </b-tabs>

  </div>
</template>
