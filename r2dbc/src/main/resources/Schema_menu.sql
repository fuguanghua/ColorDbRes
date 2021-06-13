BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE menu';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
declare
    V_NUM number;
BEGIN
    V_NUM := 0;
    select count(0) into V_NUM from user_sequences where sequence_name = 'MENU_SEQ';
    if V_NUM > 0 then
        execute immediate 'DROP SEQUENCE MENU_SEQ';
    end if;
END;
/
CREATE TABLE menu (
  id number(19) NOT NULL ,
  i_frame char check (i_frame in (0,1)),
  name varchar2(255) DEFAULT NULL ,
  component varchar2(255) DEFAULT NULL ,
  pid number(19) NOT NULL ,
  sort number(19) DEFAULT NULL ,
  icon varchar2(255) DEFAULT NULL ,
  path varchar2(255) DEFAULT NULL ,
  cache char check (cache in (0,1)) ,
  hidden char check (hidden in (0,1)) ,
  component_name varchar2(20) DEFAULT '-' ,
  create_time timestamp(0) DEFAULT NULL ,
  permission varchar2(255) DEFAULT NULL ,
  type number(10) DEFAULT NULL ,
  out_id varchar2(255) DEFAULT NULL ,
  isdisabled varchar2(255) DEFAULT '0' ,
  CONSTRAINT menu_constrain PRIMARY KEY (id)
);

COMMENT ON TABLE menu IS '系统菜单';

CREATE SEQUENCE menu_seq START WITH 140 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER menu_seq_tr
 BEFORE INSERT ON menu FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT menu_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX FKqcf9gem97gqa5qjm4d3elcqt5 ON menu (pid);

INSERT ALL
INTO MENU VALUES (1,0,'首页','home',0,1,'','home',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,NULL,'0')
INTO MENU VALUES (6,0,'系统监控',NULL,0,10,'monitor','monitor',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,'90000013','0')
INTO MENU VALUES (7,0,'操作日志','monitor/log/index',6,11,'','logs',0,0,'Log',CURRENT_TIMESTAMP,NULL,1,'90000709','0')
INTO MENU VALUES (124,0,'总部业务查询',NULL,0,100,'tab','headinq',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,'90000014','0')
INTO MENU VALUES (125,0,'技术运维',NULL,0,300,'peoples','techimain',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,'90000015','0')
INTO MENU VALUES (126,0,'总部业务处理',NULL,0,500,'tools','headbusiproc',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,'90000016','0')
INTO MENU VALUES (127,0,'客户实时成交汇总','headinq/custransum/index',124,101,'','custransum',0,0,'custransum',CURRENT_TIMESTAMP,NULL,1,'90000103','0')
INTO MENU VALUES (128,0,'双融集中度查询','headinq/mgficoinq/',124,102,'','mgficoinq',0,0,'mgficoinq',CURRENT_TIMESTAMP,NULL,1,'90000104','0')
INTO MENU VALUES (129,0,'双融强制平仓','headbusiproc/maficols/index',126,501,'','maficols',0,0,'maficols',CURRENT_TIMESTAMP,NULL,1,'90000802','1')
INTO MENU VALUES (130,0,'股质业务汇总','headinq/stplbusu/index',124,103,'','stplbusu',0,0,'stplbusu',CURRENT_TIMESTAMP,NULL,1,'90000102','0')
INTO MENU VALUES (131,0,'客户协同营业部设置','headbusiproc/cucosadese/index',126,502,'','cucosadese',0,0,'cucosadese',CURRENT_TIMESTAMP,NULL,1,'90000706','1')
INTO MENU VALUES (132,0,'公共参数分类','techimain/copacl/index',125,301,'','copacl',0,0,'copacl',CURRENT_TIMESTAMP,NULL,1,'90000701','0')
INTO MENU VALUES (133,0,'开工作业检查','techimain/stwoin',125,302,'','stwoin',0,0,'stwoin',CURRENT_TIMESTAMP,NULL,1,'90000702','0')
INTO MENU VALUES (134,0,'盘后作业检查','techimain/pstjobchk/index',125,303,'','pstjobchk',0,0,'pstjobchk',CURRENT_TIMESTAMP,NULL,1,'90000703','1')
INTO MENU VALUES (135,0,'新增营业部批量设置','techimain/batsetdept/index',125,304,'','batsetdept',0,0,'batsetdept',CURRENT_TIMESTAMP,NULL,1,'90000704','0')
INTO MENU VALUES (136,0,'营业部合并批量处理','techimain/deptmebat/index',125,505,'','deptmebat',0,0,'deptmebat',CURRENT_TIMESTAMP,NULL,1,NULL,'0')
INTO MENU VALUES (137,0,'资金账号比对','techimain/funidcmp/index',125,306,'','funidcmp',0,0,'funidcmp',CURRENT_TIMESTAMP,NULL,1,'90000705','0')
INTO MENU VALUES (138,0,'场外基金文件记录数查询','techimain/otcdocgen/index',125,307,'','otcdocgen',0,0,'otcdocgen',CURRENT_TIMESTAMP,NULL,1,'90000801','0')
INTO MENU VALUES (139,0,'柜员恢复','headbusiproc/busires/index',126,503,'','busires',0,0,'busires',CURRENT_TIMESTAMP,'',1,'90000707','0')
INTO MENU VALUES (140,0,'股票质押证金报送','headbusiproc/gpzyzjbs/index',126,506,'','gpzyzjbs',0,0,'gpzyzjbs',CURRENT_TIMESTAMP,'',1,'90000710','0')
INTO MENU VALUES (141,0,'同名转账盘后对账','techimain/script/index',125,507,'','gpzyzjbs',0,0,'script',CURRENT_TIMESTAMP,'',1,'90000711','0')
INTO MENU VALUES (142,0,'转融通借入委托明细查询','headinq/zrtcjwtcx/index',124,508,'','zrtcjwtcx',0,0,'zrtcjwtcx',CURRENT_TIMESTAMP,NULL,1,'90000712','0')
INTO MENU VALUES (143,0,'指定资金账号报送A04','headbusiproc/A04/index',126,509,'','A04',0,0,'A04',CURRENT_TIMESTAMP,NULL,1,'90000713','0')
INTO MENU VALUES (144,0,'QF客户对账单','headbusiproc/qfcustdzd/index',126,510,'','qfcustdzd',0,0,'qfcustdzd',CURRENT_TIMESTAMP,NULL,1,'90000714','0')
INTO MENU VALUES (145,0,'QF客户记录增加','headbusiproc/maficols/addCust',129,511,'','addCust',0,0,'addCust',CURRENT_TIMESTAMP,NULL,1,'90000714','0')
INTO MENU VALUES (146,0,'QF客户记录删除','headbusiproc/maficols/delCust',129,512,'','delCust',0,0,'delCust',CURRENT_TIMESTAMP,NULL,1,'90000714','0')
INTO MENU VALUES (147,0,'QF客户对账单到处','headbusiproc/maficols/outputFile',129,513,'','outputFile',0,0,'outputFile',CURRENT_TIMESTAMP,NULL,1,'90000714','0')
INTO MENU VALUES (148,0,'配置中心',NULL,0,600,'tools','deploycenter',0,0,NULL,CURRENT_TIMESTAMP,NULL,0,'90000715','0')
INTO MENU VALUES (149,0,'营业部代码配置','deploycenter/deptset/index',148,601,'','deptset',0,0,'deptset',CURRENT_TIMESTAMP,NULL,1,'90000716','0')
INTO MENU VALUES (150,0,'应用系统参数配置','deploycenter/systemset/index',148,602,'','systemset',0,0,'systemset',CURRENT_TIMESTAMP,NULL,1,'90000717','0')
INTO MENU VALUES (151,0,'节点XP队列配置','deploycenter/xpset/index',148,603,'','xpset',0,0,'xpset',CURRENT_TIMESTAMP,NULL,1,'90000718','0')
INTO MENU VALUES (152,0,'同名转账A21报送','headbusiproc/A21/index',126,514,'','A21',0,0,'A21',CURRENT_TIMESTAMP,NULL,1,'90000719','0')
INTO MENU VALUES (153,0,'集中交易挂账数据导入','headbusiproc/pendingdata/index',126,604,'','pendingdata',0,0,'pendingdata',CURRENT_TIMESTAMP,NULL,1,'90000721','0')
INTO MENU VALUES (154,0,'同名转账实时监控','monitor/tmzzssjk/index',6,515,'','tmzzssjk',0,0,'tmzzssjk',CURRENT_TIMESTAMP,NULL,1,'90000720','0')
INTO MENU VALUES (155,0,'跨法人前期准备','headbusiproc/kfrqqzb/index',126,605,'','kfrqqzb',0,0,'kfrqqzb',CURRENT_TIMESTAMP,NULL,1,'90000722','0')
INTO MENU VALUES (156,0,'账户系统客户迁移','headbusiproc/accsyscm/index',126,606,'','accsyscm',0,0,'accsyscm',CURRENT_TIMESTAMP,NULL,1,'90000723','0')
INTO MENU VALUES (157,0,'跨法人权限申报','headbusiproc/permissd/index',126,607,'','permissd',0,0,'permissd',CURRENT_TIMESTAMP,NULL,1,'90000724','0')
INTO MENU VALUES (158,0,'H股全流通额度录入','headbusiproc/Hglt/index',126,608,'','Hglt',0,0,'Hglt',CURRENT_TIMESTAMP,NULL,1,'90000725','0')
INTO MENU VALUES (159,0,'客户账户变更信息查询','headinq/CustAccAlterQuery/index',124,609,'','CustAccAlterQuery',0,0,'CustAccAlterQuery',CURRENT_TIMESTAMP,NULL,1,'90000726','0')
INTO MENU VALUES (160,0,'跨法人迁移','headbusiproc/ccmigration/index',126,610,'','ccmigration',0,0,'ccmigration',CURRENT_TIMESTAMP,NULL,1,'90000727','0')
INTO MENU VALUES (161,0,'迁移数据准备','headbusiproc/ccmigration/DataPre',160,611,'','DataPre',0,0,'DataPre',CURRENT_TIMESTAMP,NULL,1,'90000728','0')
INTO MENU VALUES (162,0,'客户迁移  ','headbusiproc/ccmigration/CustomerMigrate',160,612,'','CustomerMigrate',0,0,'CustomerMigrate',CURRENT_TIMESTAMP,NULL,1,'90000729','0')
INTO MENU VALUES (163,0,'权限申报  ','headbusiproc/ccmigration/PerDeclare',160,613,'','PerDeclare',0,0,'PerDeclare',CURRENT_TIMESTAMP,NULL,1,'90000730','0')
INTO MENU VALUES (164,0,'客户迁移明细','headbusiproc/ccmigration/MigrateRes',160,614,'','MigrateRes',0,0,'MigrateRes',CURRENT_TIMESTAMP,NULL,1,'90000731','0')
select * from dual;
COMMIT;
