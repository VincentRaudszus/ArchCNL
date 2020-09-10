/**
 * generated by Xtext 2.14.0
 */
package org.architecture.cnl.generator;

import api.APIFactory;
import api.OntologyAPI;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import datatypes.ArchitectureRule;
import datatypes.ArchitectureRules;
import datatypes.RuleType;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.architecture.cnl.archcnl.AndObjectConceptExpression;
import org.architecture.cnl.archcnl.Anything;
import org.architecture.cnl.archcnl.CanOnlyRuleType;
import org.architecture.cnl.archcnl.CardinalityRuleType;
import org.architecture.cnl.archcnl.ConceptExpression;
import org.architecture.cnl.archcnl.ConditionalRuleType;
import org.architecture.cnl.archcnl.DataStatement;
import org.architecture.cnl.archcnl.DatatypeRelation;
import org.architecture.cnl.archcnl.MustRuleType;
import org.architecture.cnl.archcnl.NegationRuleType;
import org.architecture.cnl.archcnl.Nothing;
import org.architecture.cnl.archcnl.ObjectConceptExpression;
import org.architecture.cnl.archcnl.ObjectRelation;
import org.architecture.cnl.archcnl.OnlyCanRuleType;
import org.architecture.cnl.archcnl.OrObjectConceptExpression;
import org.architecture.cnl.archcnl.Relation;
import org.architecture.cnl.archcnl.Sentence;
import org.architecture.cnl.archcnl.StatementList;
import org.architecture.cnl.archcnl.SubConceptRuleType;
import org.architecture.cnl.archcnl.ThatExpression;
import org.architecture.cnl.archcnl.VariableStatement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
@SuppressWarnings("all")
public class ArchcnlGenerator extends AbstractGenerator {
  private static final Logger LOG = LogManager.getLogger(AbstractGenerator.class);
  
  private String namespace;
  
  private OntologyAPI api;
  
  private static int id = 0;
  
  private Iterable<EObject> resourceIterable;
  
  private Iterable<Sentence> sentenceIterable;
  
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    try {
      this.namespace = "http://www.arch-ont.org/ontologies/architecture.owl";
      this.api = APIFactory.get();
      this.api.createOntology((("./architecture" + Integer.valueOf(ArchcnlGenerator.id)) + ".owl"), this.namespace);
      this.resourceIterable = IteratorExtensions.<EObject>toIterable(resource.getAllContents());
      this.sentenceIterable = Iterables.<Sentence>filter(this.resourceIterable, Sentence.class);
      ArchcnlGenerator.LOG.info("Start compiling sentences ...");
      for (final Sentence s : this.sentenceIterable) {
        {
          ConceptExpression _subject = s.getSubject();
          String _plus = (((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "sentence subject: ") + _subject);
          ArchcnlGenerator.LOG.info(_plus);
          EObject _ruletype = s.getRuletype();
          String _plus_1 = (((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "sentence ruletype: ") + _ruletype);
          ArchcnlGenerator.LOG.info(_plus_1);
          this.compile(s);
        }
      }
      ArchcnlGenerator.LOG.info("compiled all sentences");
      this.api.removeOntology(this.namespace);
      final File f = new File((("architecture" + Integer.valueOf(ArchcnlGenerator.id)) + ".owl"));
      Files.deleteIfExists(f.toPath());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void compile(final Sentence s) {
    final ConceptExpression subject = s.getSubject();
    final EObject ruletype = s.getRuletype();
    final ArchitectureRules rules = ArchitectureRules.getInstance();
    String _cnlSentence = rules.getRuleWithID(ArchcnlGenerator.id).getCnlSentence();
    String _plus = ((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + _cnlSentence);
    ArchcnlGenerator.LOG.info(_plus);
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + ruletype));
    if ((ruletype instanceof MustRuleType)) {
      this.compile(((MustRuleType)ruletype), subject);
      ArchitectureRule _ruleWithID = rules.getRuleWithID(ArchcnlGenerator.id);
      _ruleWithID.setType(RuleType.EXISTENTIAL);
    } else {
      if ((ruletype instanceof CanOnlyRuleType)) {
        this.compile(((CanOnlyRuleType)ruletype), subject);
        ArchitectureRule _ruleWithID_1 = rules.getRuleWithID(ArchcnlGenerator.id);
        _ruleWithID_1.setType(RuleType.UNIVERSAL);
      } else {
        if ((ruletype instanceof OnlyCanRuleType)) {
          this.compile(((OnlyCanRuleType)ruletype));
          ArchitectureRule _ruleWithID_2 = rules.getRuleWithID(ArchcnlGenerator.id);
          _ruleWithID_2.setType(RuleType.DOMAIN_RANGE);
        } else {
          if ((ruletype instanceof ConditionalRuleType)) {
            this.compile(((ConditionalRuleType)ruletype));
            ArchitectureRule _ruleWithID_3 = rules.getRuleWithID(ArchcnlGenerator.id);
            _ruleWithID_3.setType(RuleType.CONDITIONAL);
          } else {
            if ((ruletype instanceof NegationRuleType)) {
              this.compile(((NegationRuleType)ruletype));
              ArchitectureRule _ruleWithID_4 = rules.getRuleWithID(ArchcnlGenerator.id);
              _ruleWithID_4.setType(RuleType.NEGATION);
            } else {
              if ((ruletype instanceof SubConceptRuleType)) {
                this.compile(((SubConceptRuleType)ruletype), subject);
                ArchitectureRule _ruleWithID_5 = rules.getRuleWithID(ArchcnlGenerator.id);
                _ruleWithID_5.setType(RuleType.SUB_CONCEPT);
              } else {
                if ((ruletype instanceof CardinalityRuleType)) {
                  this.compile(((CardinalityRuleType)ruletype), subject);
                }
              }
            }
          }
        }
      }
    }
    ArchcnlGenerator.LOG.info((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": sentence processed"));
    ArchcnlGenerator.id++;
  }
  
  public void compile(final SubConceptRuleType subconcept, final ConceptExpression subject) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling SubConceptRuleType ..."));
    final OWLClassExpression subjectConceptExpression = this.compile(subject);
    final OWLClass object = this.api.getOWLClass(this.namespace, subconcept.getObject().getConcept().getConceptName());
    this.api.addSubClassAxiom(this.namespace, object, subjectConceptExpression);
  }
  
  public void compile(final CardinalityRuleType cardrule, final ConceptExpression subject) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling CardinalityRuleType ..."));
    final OWLClassExpression subjectConceptExpression = this.compile(subject);
    OWLClassExpression object = this.compile(cardrule.getObject().getExpression());
    final ArrayList<OWLClassExpression> listResult = new ArrayList<OWLClassExpression>();
    listResult.add(object);
    EList<AndObjectConceptExpression> _objectAndList = cardrule.getObject().getObjectAndList();
    for (final AndObjectConceptExpression o : _objectAndList) {
      {
        final OWLClassExpression result = this.compile(o.getExpression());
        listResult.add(result);
      }
    }
    int _size = listResult.size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      object = this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(listResult, OWLClassExpression.class)));
      listResult.clear();
    }
    EList<OrObjectConceptExpression> _objectOrList = cardrule.getObject().getObjectOrList();
    for (final OrObjectConceptExpression o_1 : _objectOrList) {
      {
        final OWLClassExpression result = this.compile(o_1.getExpression());
        listResult.add(result);
      }
    }
    int _size_1 = listResult.size();
    boolean _greaterThan_1 = (_size_1 > 1);
    if (_greaterThan_1) {
      object = this.api.unionOf(this.namespace, listResult);
      listResult.clear();
    }
    this.api.addSubClassAxiom(this.namespace, object, subjectConceptExpression);
  }
  
  public void compile(final NegationRuleType negation) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling NegationRuleType ..."));
    if ((negation instanceof Nothing)) {
      final OWLClassExpression subject = this.api.getOWLTop(this.namespace);
      OWLClassExpression object = this.compile(((Nothing)negation).getObject().getExpression());
      final ArrayList<OWLClassExpression> listResult = new ArrayList<OWLClassExpression>();
      listResult.add(object);
      EList<AndObjectConceptExpression> _objectAndList = ((Nothing)negation).getObject().getObjectAndList();
      for (final AndObjectConceptExpression o : _objectAndList) {
        {
          final OWLClassExpression result = this.compile(o.getExpression());
          listResult.add(result);
        }
      }
      int _size = listResult.size();
      boolean _greaterThan = (_size > 1);
      if (_greaterThan) {
        object = this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(listResult, OWLClassExpression.class)));
        listResult.clear();
      }
      EList<OrObjectConceptExpression> _objectOrList = ((Nothing)negation).getObject().getObjectOrList();
      for (final OrObjectConceptExpression o_1 : _objectOrList) {
        {
          final OWLClassExpression result = this.compile(o_1.getExpression());
          listResult.add(result);
        }
      }
      int _size_1 = listResult.size();
      boolean _greaterThan_1 = (_size_1 > 1);
      if (_greaterThan_1) {
        object = this.api.unionOf(this.namespace, listResult);
        listResult.clear();
      }
      this.api.addNegationAxiom(this.namespace, subject, object);
    } else {
      final OWLClassExpression subjectConceptExpression = this.compile(negation.getSubject());
      Anything _anything = negation.getObject().getAnything();
      boolean _tripleNotEquals = (_anything != null);
      if (_tripleNotEquals) {
        OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, 
          negation.getObject().getAnything().getRelation().getRelationName());
        final OWLObjectProperty relation = ((OWLObjectProperty) _oWLObjectProperty);
        OWLClassExpression object_1 = this.api.getOWLTop(this.namespace);
        this.api.addNegationAxiom(this.namespace, subjectConceptExpression, object_1, relation);
      } else {
        OWLClassExpression object_2 = this.compile(negation.getObject().getExpression());
        final ArrayList<OWLClassExpression> listResult_1 = new ArrayList<OWLClassExpression>();
        listResult_1.add(object_2);
        EList<AndObjectConceptExpression> _objectAndList_1 = negation.getObject().getObjectAndList();
        for (final AndObjectConceptExpression o_2 : _objectAndList_1) {
          {
            final OWLClassExpression result = this.compile(o_2.getExpression());
            listResult_1.add(result);
          }
        }
        int _size_2 = listResult_1.size();
        boolean _greaterThan_2 = (_size_2 > 1);
        if (_greaterThan_2) {
          object_2 = this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(listResult_1, OWLClassExpression.class)));
          listResult_1.clear();
        }
        EList<OrObjectConceptExpression> _objectOrList_1 = negation.getObject().getObjectOrList();
        for (final OrObjectConceptExpression o_3 : _objectOrList_1) {
          {
            final OWLClassExpression result = this.compile(o_3.getExpression());
            listResult_1.add(result);
          }
        }
        int _size_3 = listResult_1.size();
        boolean _greaterThan_3 = (_size_3 > 1);
        if (_greaterThan_3) {
          object_2 = this.api.unionOf(this.namespace, listResult_1);
          listResult_1.clear();
        }
        this.api.addNegationAxiom(this.namespace, subjectConceptExpression, object_2);
      }
    }
  }
  
  public void compile(final ConditionalRuleType conditional) {
    this.compile(conditional.getSubject());
    this.compile(conditional.getObject());
    this.api.addSubPropertyOfAxiom(this.namespace, conditional.getRelation().getRelationName(), conditional.getRelation2().getRelationName());
  }
  
  public void compile(final OnlyCanRuleType onlycan) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling OnlyCanRuleType ..."));
    final OWLClassExpression subjectConceptExpression = this.compile(onlycan.getSubject());
    OWLClassExpression object = this.compile(onlycan.getObject().getExpression().getConcept());
    OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, 
      onlycan.getObject().getExpression().getRelation().getRelationName());
    OWLObjectProperty relation = ((OWLObjectProperty) _oWLObjectProperty);
    final EList<OrObjectConceptExpression> objectOrList = onlycan.getObject().getObjectOrList();
    final ArrayList<OWLClassExpression> listResult = new ArrayList<OWLClassExpression>();
    listResult.add(object);
    for (final OrObjectConceptExpression o : objectOrList) {
      {
        final OWLClassExpression result = this.compile(o.getExpression().getConcept());
        listResult.add(result);
      }
    }
    object = this.api.unionOf(this.namespace, listResult);
    this.api.addDomainRangeAxiom(this.namespace, subjectConceptExpression, object, relation);
  }
  
  public void compile(final CanOnlyRuleType canonly, final ConceptExpression subject) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling CanOnlyRuleType ..."));
    final OWLClassExpression subjectConceptExpression = this.compile(subject);
    OWLClassExpression object = this.compile(canonly.getObject().getExpression().getConcept());
    OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, 
      canonly.getObject().getExpression().getRelation().getRelationName());
    OWLObjectProperty relation = ((OWLObjectProperty) _oWLObjectProperty);
    final EList<AndObjectConceptExpression> objectAndList = canonly.getObject().getObjectAndList();
    final EList<OrObjectConceptExpression> objectOrList = canonly.getObject().getObjectOrList();
    final ArrayList<OWLClassExpression> listResult = new ArrayList<OWLClassExpression>();
    object = this.api.createOnlyRestriction(this.namespace, relation, object);
    listResult.add(object);
    for (final AndObjectConceptExpression o : objectAndList) {
      {
        OWLClassExpression result = this.compile(o.getExpression().getConcept());
        result = this.api.createOnlyRestriction(this.namespace, relation, result);
        listResult.add(result);
      }
    }
    int _size = listResult.size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      object = this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(listResult, OWLClassExpression.class)));
      listResult.clear();
    }
    for (final OrObjectConceptExpression o_1 : objectOrList) {
      {
        OWLClassExpression result = this.compile(o_1.getExpression().getConcept());
        result = this.api.createOnlyRestriction(this.namespace, relation, result);
        listResult.add(result);
      }
    }
    int _size_1 = listResult.size();
    boolean _greaterThan_1 = (_size_1 > 1);
    if (_greaterThan_1) {
      object = this.api.unionOf(this.namespace, listResult);
      listResult.clear();
    }
    this.api.addSubClassAxiom(this.namespace, object, subjectConceptExpression);
  }
  
  public void compile(final MustRuleType must, final ConceptExpression subject) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling MustRuleType ... "));
    final OWLClassExpression subjectConceptExpression = this.compile(subject);
    OWLClassExpression object = this.compile(must.getObject().getExpression().getConcept());
    OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, 
      must.getObject().getExpression().getRelation().getRelationName());
    OWLObjectProperty relation = ((OWLObjectProperty) _oWLObjectProperty);
    final EList<AndObjectConceptExpression> objectAndList = must.getObject().getObjectAndList();
    final EList<OrObjectConceptExpression> objectOrList = must.getObject().getObjectOrList();
    final ArrayList<OWLClassExpression> listResult = new ArrayList<OWLClassExpression>();
    object = this.api.addSomeValuesFrom(this.namespace, relation, object);
    listResult.add(object);
    for (final AndObjectConceptExpression o : objectAndList) {
      {
        OWLClassExpression result = this.compile(o.getExpression().getConcept());
        result = this.api.addSomeValuesFrom(this.namespace, relation, result);
        listResult.add(result);
      }
    }
    int _size = listResult.size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      object = this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(listResult, OWLClassExpression.class)));
      listResult.clear();
    }
    for (final OrObjectConceptExpression o_1 : objectOrList) {
      {
        OWLClassExpression result = this.compile(o_1.getExpression().getConcept());
        result = this.api.addSomeValuesFrom(this.namespace, relation, result);
        listResult.add(result);
      }
    }
    int _size_1 = listResult.size();
    boolean _greaterThan_1 = (_size_1 > 1);
    if (_greaterThan_1) {
      object = this.api.unionOf(this.namespace, listResult);
      listResult.clear();
    }
    this.api.addSubClassAxiom(this.namespace, object, subjectConceptExpression);
  }
  
  public OWLClassExpression compile(final ObjectConceptExpression object) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling ObjectConceptExpression ..."));
    OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, object.getRelation().getRelationName());
    final OWLObjectProperty relation = ((OWLObjectProperty) _oWLObjectProperty);
    final OWLClassExpression concept = this.compile(object.getConcept());
    final int count = object.getNumber();
    String _cardinality = object.getCardinality();
    boolean _equals = Objects.equal(_cardinality, "at-most");
    if (_equals) {
      ArchitectureRule _ruleWithID = ArchitectureRules.getInstance().getRuleWithID(ArchcnlGenerator.id);
      _ruleWithID.setType(RuleType.AT_MOST);
      return this.api.addMaxCardinalityRestrictionAxiom(this.namespace, concept, relation, count);
    } else {
      String _cardinality_1 = object.getCardinality();
      boolean _equals_1 = Objects.equal(_cardinality_1, "at-least");
      if (_equals_1) {
        ArchitectureRule _ruleWithID_1 = ArchitectureRules.getInstance().getRuleWithID(ArchcnlGenerator.id);
        _ruleWithID_1.setType(RuleType.AT_LEAST);
        return this.api.addMinCardinalityRestrictionAxiom(this.namespace, concept, relation, count);
      } else {
        String _cardinality_2 = object.getCardinality();
        boolean _equals_2 = Objects.equal(_cardinality_2, "exactly");
        if (_equals_2) {
          ArchitectureRule _ruleWithID_2 = ArchitectureRules.getInstance().getRuleWithID(ArchcnlGenerator.id);
          _ruleWithID_2.setType(RuleType.EXACTLY);
          return this.api.addExactCardinalityRestrictionAxiom(this.namespace, concept, relation, count);
        } else {
          return this.api.addSomeValuesFrom(this.namespace, relation, concept);
        }
      }
    }
  }
  
  public OWLClassExpression compile(final ConceptExpression conceptExpression) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling ConceptExpression ... "));
    final OWLClass conceptAsOWL = this.api.getOWLClass(this.namespace, conceptExpression.getConcept().getConceptName());
    OWLClassExpression result = ((OWLClassExpression) conceptAsOWL);
    final EList<ThatExpression> thatList = conceptExpression.getThat();
    boolean _isEmpty = thatList.isEmpty();
    if (_isEmpty) {
      return result;
    } else {
      final ThatExpression that = thatList.get(0);
      result = this.compile(that);
      result = this.api.intersectionOf(this.namespace, conceptAsOWL, result);
      return result;
    }
  }
  
  public OWLClassExpression compile(final ThatExpression that) {
    ArchcnlGenerator.LOG.info(((("ID " + Integer.valueOf(ArchcnlGenerator.id)) + ": ") + "compiling ThatExpression ..."));
    ArrayList<OWLClassExpression> results = new ArrayList<OWLClassExpression>();
    EList<StatementList> _list = that.getList();
    for (final StatementList statements : _list) {
      {
        final EObject expression = statements.getExpression();
        if ((expression instanceof ConceptExpression)) {
          Relation _relation = statements.getRelation();
          final ObjectRelation relation = ((ObjectRelation) _relation);
          OWLProperty _oWLObjectProperty = this.api.getOWLObjectProperty(this.namespace, relation.getRelationName());
          final OWLObjectProperty thatRoleOWL = ((OWLObjectProperty) _oWLObjectProperty);
          final OWLClassExpression owlexpression = this.compile(((ConceptExpression)expression));
          OWLClassExpression result = this.api.addSomeValuesFrom(this.namespace, thatRoleOWL, owlexpression);
          results.add(result);
        } else {
          if ((expression instanceof DataStatement)) {
            InputOutput.<Relation>println(statements.getRelation());
            Relation _relation_1 = statements.getRelation();
            final DatatypeRelation relation_1 = ((DatatypeRelation) _relation_1);
            OWLProperty _oWLDatatypeProperty = this.api.getOWLDatatypeProperty(this.namespace, relation_1.getRelationName());
            final OWLDataProperty thatRoleOWL_1 = ((OWLDataProperty) _oWLDatatypeProperty);
            final String dataString = ((DataStatement)expression).getStringValue();
            if ((dataString != null)) {
              final OWLDataHasValue dataHasValue = this.api.addDataHasValue(this.namespace, dataString, thatRoleOWL_1);
              results.add(dataHasValue);
            } else {
              final OWLDataHasValue dataHasValue_1 = this.api.addDataHasIntegerValue(this.namespace, ((DataStatement)expression).getIntValue(), thatRoleOWL_1);
              results.add(dataHasValue_1);
            }
          } else {
            if ((expression instanceof VariableStatement)) {
              InputOutput.<String>println("with Variable");
              return null;
            }
          }
        }
      }
    }
    final ArrayList<OWLClassExpression> _converted_results = (ArrayList<OWLClassExpression>)results;
    return this.api.intersectionOf(this.namespace, ((OWLClassExpression[])Conversions.unwrapArray(_converted_results, OWLClassExpression.class)));
  }
  
  public OWLClassExpression intersectionOf(final OntologyAPI api, final String string, final OWLClassExpression expression1, final OWLClassExpression expression2) {
    return api.intersectionOf(this.namespace, expression1, expression2);
  }
  
  public OWLClass getOWLClass(final OntologyAPI api, final String string, final String string2) {
    return api.getOWLClass(string, string2);
  }
}
