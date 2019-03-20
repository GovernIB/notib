package es.caib.notib.core.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;


/**
* Default implementation of {@link AclAuthorizationStrategy}.
* <p>
* Permission will be granted if at least one of the following conditions is true for the current 
* principal. 
* <ul>
* <li> is the owner (as defined by the ACL). </li>
* <li> holds the relevant system-wide {@link GrantedAuthority} injected into the 
*      constructor. </li>
* <li> has {@link BasePermission#ADMINISTRATION} permission (as defined by the ACL). </li>
* </ul>
*
* @author Ben Alex
*/
public class AclAuthorizationStrategyImpl implements AclAuthorizationStrategy {
   //~ Instance fields ================================================================================================

   private final GrantedAuthority entitatGaGeneralChanges;
   private final GrantedAuthority entitatGaModifyAuditing;
   private final GrantedAuthority entitatGaTakeOwnership;
   private final GrantedAuthority procedimentGaGeneralChanges;
   private final GrantedAuthority procedimentGaModifyAuditing;
   private final GrantedAuthority procedimentGaTakeOwnership;
   private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();

   //~ Constructors ===================================================================================================

   /**
    * Constructor. The only mandatory parameter relates to the system-wide {@link GrantedAuthority} instances that
    * can be held to always permit ACL changes.
    *
    * @param auths the <code>GrantedAuthority</code>s that have
    * special permissions (index 0 is the authority needed to change
    * ownership, index 1 is the authority needed to modify auditing details,
    * index 2 is the authority needed to change other ACL and ACE details) (required)
    * <p>
    * Alternatively, a single value can be supplied for all three permissions.
    */
   public AclAuthorizationStrategyImpl(GrantedAuthority... auths) {
       Assert.isTrue(auths != null && (auths.length == 6 || auths.length == 2),
               "Two or six GrantedAuthority instances required");
       if (auths.length == 6) {
    	   entitatGaTakeOwnership = auths[0];
    	   entitatGaModifyAuditing = auths[1];
           entitatGaGeneralChanges = auths[2];
           procedimentGaTakeOwnership = auths[3];
           procedimentGaModifyAuditing = auths[4];
           procedimentGaGeneralChanges = auths[5];
       } else {
    	   entitatGaTakeOwnership = entitatGaModifyAuditing = entitatGaGeneralChanges = auths[0];
    	   procedimentGaTakeOwnership = procedimentGaModifyAuditing = procedimentGaGeneralChanges = auths[1];
       }
   }

   //~ Methods ========================================================================================================

   public void securityCheck(Acl acl, int changeType) {
       if ((SecurityContextHolder.getContext() == null)
           || (SecurityContextHolder.getContext().getAuthentication() == null)
           || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
           throw new AccessDeniedException("Authenticated principal required to operate with ACLs");
       }

       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

       // Check if authorized by virtue of ACL ownership
       Sid currentUser = new PrincipalSid(authentication);

       if (currentUser.equals(acl.getOwner())
               && ((changeType == CHANGE_GENERAL) || (changeType == CHANGE_OWNERSHIP))) {
           return;
       }

       // Not authorized by ACL ownership; try via adminstrative permissions
       GrantedAuthority requiredAuthority;

       String tipus = acl.getObjectIdentity().getType();
       
       if ("es.caib.notib.core.entity.ProcedimentEntity".equals(tipus)) {
    	   if (changeType == CHANGE_AUDITING) {
	           requiredAuthority = this.procedimentGaModifyAuditing;
	       } else if (changeType == CHANGE_GENERAL) {
	           requiredAuthority = this.procedimentGaGeneralChanges;
	       } else if (changeType == CHANGE_OWNERSHIP) {
	           requiredAuthority = this.procedimentGaTakeOwnership;
	       } else {
	           throw new IllegalArgumentException("Unknown change type");
	       }
       } else {
    	   if (changeType == CHANGE_AUDITING) {
	           requiredAuthority = this.entitatGaModifyAuditing;
	       } else if (changeType == CHANGE_GENERAL) {
	           requiredAuthority = this.entitatGaGeneralChanges;
	       } else if (changeType == CHANGE_OWNERSHIP) {
	           requiredAuthority = this.entitatGaTakeOwnership;
	       } else {
	           throw new IllegalArgumentException("Unknown change type");
	       }
       }

       // Iterate this principal's authorities to determine right
       if (authentication.getAuthorities().contains(requiredAuthority)) {
           return;
       }

       // Try to get permission via ACEs within the ACL
       List<Sid> sids = sidRetrievalStrategy.getSids(authentication);

       if (acl.isGranted(Arrays.asList(BasePermission.ADMINISTRATION), sids, false)) {
           return;
       }

       throw new AccessDeniedException(
               "Principal does not have required ACL permissions to perform requested operation");
   }

   public void setSidRetrievalStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
       Assert.notNull(sidRetrievalStrategy, "SidRetrievalStrategy required");
       this.sidRetrievalStrategy = sidRetrievalStrategy;
   }
}
