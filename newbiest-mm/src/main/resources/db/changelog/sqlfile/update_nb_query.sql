update nb_query n set n.query_text = 'select  n.model_id, n.sub_code,n.model_class from MM_MODEL_RELATION@TO_MES n' where n.name = 'queryProductSubcode';
