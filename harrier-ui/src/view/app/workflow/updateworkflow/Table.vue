<template>
  <div>
    <div class="spdb-form">
      <Form ref="workflowManageForm" :model="formBean" :label-width="75" :rules='ruleValidate'>
        <Row type="flex" justify="center" align="middle">
          <Col span="20">
            <Row>
              <p>任务变更--同步生成作业编排信息</p>
            </Row>
            <Row style="margin-top:10px">
              <Card>
                <Row>
                  <Col span="3">
                    <FormItem prop="platform" label="平台名">
                      <Select :disabled="isEdit" filterable v-model="formBean.platform" clearable  @on-change="querySystem">
                      <Option v-for="item in platformData" :value="item.value" :key="item.value">{{ item.label }}</Option>
                      </Select>
                    </FormItem>
                  </Col>
                  <Col span="4">
                    <Form-Item prop="systems" label="应用名">
                      <Select ref="refsystem" :disabled="isEdit" v-model="formBean.systems" @on-change="queryJob" filterable clearable>
                      <Option v-for="item in systemsData" :value="item.value" :key="item.key">{{ item.label }}</Option>
                      </Select>
                    </Form-Item>
                  </Col>
                  <Col span="8">
                    <Form-Item prop="job" label="作业名">
                      <!-- <Input  v-model="formBean.job"/> -->
                      <Select ref="refjob" v-model="formBean.job" filterable clearable>
                        <Option v-for="item in jobData" :value="item.value" :key="item.key">{{ item.label }}</Option>
                      </Select>
                    </Form-Item>
                  </Col>
                  <Col span="4">
                    <FormItem prop="taskStatus" label="任务状态">
                      <Select filterable v-model="formBean.taskStatus" clearable>
                        <Option v-for="item in taskStatusData" :value="item.value" :key="item.key">{{ item.label }}</Option>
                      </Select>
                    </FormItem>
                  </Col>
                  <Col :offset="1" span="4">
                    <div class="spdb-toolbar">
                      <Button type="primary" icon="ios-search" @click="generateJobArrange">生成编排信息</Button>
                    </div>
                  </Col>
                </Row>
              </Card>
            </Row>
          </Col>
        </Row>
      </Form>
    </div>
	</div>
</template>

<script>
import utils from '@/common/utils'

const RESOURCE_PATH = '/jobarrange'

export default {
  props: {
    transData: {
      type: Object,
      default: function () {
    			return {}
      }
    }
  },
  data: function () {
    return {
      platformData: [],
      systemsData: [],
      jobData: [],
      ctrlDisable: true,
      isEdit: false,
      formBean: {},
      platformList: [],
      taskStatusData: [
        // 1->新增；2->变更；3->下线；4->上线完成',
        { label: '变更', value: 2 },
        { label: '下线', value: 3 }
      ],
      gridData: [],
      selection: [],
      page: {
        current: 1,
        total: 10,
        size: 10,
        opt: [10, 50, 100]
      },
      ruleValidate: {
        platform: [{
          required: true,
          message: '请输入数据！'
        }],
        systems: [{
          required: true,
          message: '请输入数据！'
        }],
        job: [{
          required: true,
          message: '请输入数据！'
        }],
        taskStatus: [{
          required: true,
          message: '请输入数据！'
        }]
      }
    }
  },
  watch: {
    selection: function (val) {
      if (val.length != 0) {
        this.ctrlDisable = false
      } else {
        this.ctrlDisable = true
      }
    }
  },
  methods: {
    queryPlatform () {
      let platformList = this.$store.getters.getUserPlatform()
      platformList.forEach(data => {
        let tmp = {}
        tmp.value = data
        tmp.label = data
        this.platformData.push(tmp)
      })
    },
    querySystem () {
      this.systemsData = []
      this.$refs.refsystem.setQuery('')
      if (this.formBean.platform) {
        let userSystemList = this.$store.getters.getUserSystem()
        let userSystem = userSystemList[this.formBean.platform]
        userSystem.forEach(data => {
          let tmp = {}
          tmp.value = data
          tmp.label = data
          this.systemsData.push(tmp)
        })
      }
    },
    queryJob () {
      this.jobData = []
      this.$refs.refsystem.setQuery('')
      if (this.formBean.platform && this.formBean.systems) {
        let params = {}
        params.platform = this.formBean.platform
        params.systems = this.formBean.systems
        let loadConfig = {
          method: 'GET',
          url: '/udsJob/selectJobList',
          params: params
        }
        this.$ajax(loadConfig)
          .then(resp => {
            if (resp.status && resp.status === 200) {
              resp.data.forEach(e => {
                let tmp = {}
                tmp.value = e.job
                tmp.label = e.job
                this.jobData.push(tmp)
              })
            }
          })
      }
    },
    /**
		 * 初始化
		 **/
    init () {
      if (this.transData.etlPlatform) {
        this.formBean.etlPlatform = this.transData.etlPlatform
      }
      if (this.transData.currentPage) {
        this.page.current = this.transData.currentPage
        this.page.size = this.transData.pageSize
      }
      this.search()
    },
    /**
		 * 查询
		 **/
    search () {
      let params = {}
      Object.assign(params, this.formBean)
      params.currentPage = this.page.current
      params.pageSize = this.page.size
      if (this.formBean.job) {
        params.currentPage = 1
      }
      let httpConfig = {
        method: 'GET',
        url: '/udsJob/selectAll',
        params: params
      }

      this.$ajax(httpConfig)
        .then(resp => {
          this.gridData = resp.data.records
          this.page.total = resp.data.total
        })
    },
    /**
		 * 查询
		 **/
    generateJobArrange () {
      let params = { platform: this.formBean.platform, systems: this.formBean.systems, job: this.formBean.job, taskStatus: this.formBean.taskStatus }
      // Object.assign(params, this.formBean)
      let httpConfig = {
        method: 'GET',
        url: RESOURCE_PATH + '/generateJobArrange',
        params: params
      }

      this.$ajax(httpConfig)
        .then(resp => {
          this.$router.push({ path: '/workflow/workflowmanage' })
        })
    }
  },
  /**
	 * 视图挂载
	 **/
  mounted () {
		 this.init()
    this.queryPlatform()
  }
}
</script>
<style>
.ivu-poptip-body-content-inner {
    text-align: center;
    /* overflow: auto; */
    width: 100%;
    word-break: break-word;
}
</style>
<style scoped>
.detail-title{
  font-size: 15px;
}
.detail-message {
  color: #3e3f4b;
  margin-left: 10px;
  font-size: 13px;
  font-weight: bold;
}
</style>
