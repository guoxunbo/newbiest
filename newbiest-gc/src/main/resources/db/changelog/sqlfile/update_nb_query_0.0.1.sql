update nb_query n set n.query_text = 'select  n.model_id, n.conversion_model_id, n.model_Category from MM_MODEL_CONVERSION@TO_MES n' where n.name = 'queryProductModelConversion';
