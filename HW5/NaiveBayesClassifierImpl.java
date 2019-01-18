import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	private Instance[] m_trainingData;
	private int m_v;
	private double m_delta;
	public int m_sports_count, m_business_count;
	public int m_sports_word_count, m_business_word_count;
	private HashMap<String,Integer> m_map[] = new HashMap[2];

  @Override
  public void train(Instance[] trainingData, int v) {
  	  m_trainingData = trainingData;
  	  m_v = v;
  	  m_map[0] = new HashMap<>();
  	  m_map[1] = new HashMap<>();
  	  
  	documents_per_label_count(m_trainingData);
  	
	for(Instance instance :trainingData) 
	{
		if(instance.label.equals(Label.SPORTS)) 
		{
			for(String words : instance.words) 
			{
				if(m_map[0].containsKey(words)) 
				{
					int count = m_map[0].get(words);
					
					m_map[0].put(words, count+1);
				}
				else 
				{
					m_map[0].put(words, 1);
				}
			}
		}
		else if(instance.label.equals(Label.BUSINESS)) 
		{
			for(String words : instance.words) 
			{
				if(m_map[1].containsKey(words)) 
				{
					int count = m_map[1].get(words);
					
					m_map[1].put(words, count+1);
				}
				else 
				{
					m_map[1].put(words, 1);
				}
			}
		}
	}
  }

  public void documents_per_label_count(Instance[] trainingData)
  {
    m_sports_count = 0;
    m_business_count = 0;
    
	for (Instance instance : trainingData) 
	{
		if (instance.label.equals(Label.SPORTS)) 
		{
			m_sports_count++;
		} 
		else if (instance.label.equals(Label.BUSINESS)) 
		{
			m_business_count++;
		}
	}
}

  public void print_documents_per_label_count()
  {
  	  System.out.println("SPORTS=" + m_sports_count);
  	  System.out.println("BUSINESS=" + m_business_count);
  }

  public void words_per_label_count(Instance[] trainingData)
  {
    m_sports_word_count = 0;
    m_business_word_count = 0;
    
    for(Instance instance: trainingData) 
    {
    	if(instance.label.equals(Label.SPORTS)) 
    	{
    		m_sports_word_count += instance.words.length;
    	} 
    	else if (instance.label.equals(Label.BUSINESS)) 
    	{
    		m_business_word_count += instance.words.length;
    	}
    }
  }

  public void print_words_per_label_count()
  {
  	  System.out.println("SPORTS=" + m_sports_word_count);
  	  System.out.println("BUSINESS=" + m_business_word_count);
  }

  @Override
  public double p_l(Label label) 
  {
		double ret = (double) m_business_count / (m_sports_count + m_business_count);
		
		if (label.equals(Label.BUSINESS)) 
		{
			return ret;
		} 
		else 
		{
			return (1 - ret);
		}
  }

  @Override
  public double p_w_given_l(String word, Label label) {
    double ret = 0;
    m_delta = 0.00001;
    int sNumber = 0;
	int bNumber = 0;
	
	for (Integer integer : m_map[0].values()) 
	{
		sNumber += integer;
	}
	for (Integer integer : m_map[1].values()) 
	{
		bNumber += integer;
	}
	if (label.equals(Label.SPORTS)) 
	{
		if (m_map[0].containsKey(word)) 
		{
			Integer i = m_map[0].get(word);
			
			ret = (i + m_delta) / (m_v * m_delta + sNumber);
		} 
		else 
		{
			ret = (0 + m_delta) / (m_v * m_delta + sNumber);
		}
	}

	else if (label.equals(Label.BUSINESS)) 
	{
		if (m_map[1].containsKey(word)) 
		{
			Integer i = m_map[1].get(word);
			
			ret = (i + m_delta) / (m_v * m_delta + bNumber);
		}
		else 
		{
			ret = (0 + m_delta) / (m_v * m_delta + bNumber);
		}

	}
    return ret;
  }

  @Override
  public ClassifyResult classify(String[] words) 
  {
    ClassifyResult ret = new ClassifyResult();
    ret.label = Label.BUSINESS;
    ret.log_prob_sports = 0;
    ret.log_prob_business = 0;
	ret.log_prob_sports = Math.log(p_l(Label.SPORTS));
	ret.log_prob_business = Math.log(p_l(Label.BUSINESS));
	
	for(String w : words) 
	{
		ret.log_prob_sports += Math.log(p_w_given_l(w,Label.SPORTS));
	}	
	for(String w : words) 
	{
		ret.log_prob_business += Math.log(p_w_given_l(w,Label.BUSINESS));
	}
	if(ret.log_prob_sports > ret.log_prob_business) 
	{
		ret.label = Label.SPORTS;
	}
	return ret;
  }
  
  @Override
  public ConfusionMatrix calculate_confusion_matrix(Instance[] testData)
  {
    int TP, FP, FN, TN;
    TP = 0;
    FP = 0;
    FN = 0;
    TN = 0;
    for (Instance instance : testData) 
    {
		ClassifyResult classify = classify(instance.words);
		
		if (instance.label.equals(Label.SPORTS)) 
		{	
			if (classify.label.equals(Label.SPORTS)) 
			{
				TP++;
			} 
			else 
			{
				FN++;
			}

		} 
		else if (instance.label.equals(Label.BUSINESS)) 
		{
			if (classify.label.equals(Label.BUSINESS)) 
			{
				TN++;
			} 
			else 
			{
				FP++;
			}
		}
	}
	return new ConfusionMatrix(TP, FP, FN, TN);
  }
}
